import 'dart:convert';
import 'package:get_it/get_it.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:mobile/data/models/saved_credential.dart';
import 'package:mobile/data/models/user_credentials.dart';
import 'package:mobile/data/services/auth_api_service.dart';

class StorageService {
  static const String _boxName = 'auth_storage';
  static const String _savedCredentialsBox = 'saved_credentials_box';

  static const String _keyUserCredentials = 'user_credentials';
  static const String _keyAccessToken = 'access_token';
  static const String _keyRefreshToken = 'refresh_token';
  static const String _keyWebSocketToken = 'websocket_token';

  late Box _box;
  late Box<SavedCredential> _savedCredentialsBoxRef;
  // Make AuthApiService optional so StorageService can be initialized
  // before AuthApiService is registered in GetIt to avoid circular deps.
  AuthApiService? authApiService;

  StorageService({this.authApiService});

  Future<StorageService> init() async {
    await Hive.initFlutter();

    if (!Hive.isAdapterRegistered(0)) {
      Hive.registerAdapter(SavedCredentialAdapter());
    }

    _box = await Hive.openBox(_boxName);
    _savedCredentialsBoxRef = await Hive.openBox<SavedCredential>(_savedCredentialsBox);

    // Do NOT attempt token refresh here because AuthApiService may not
    // be registered yet. Callers should invoke `validateOrRefreshTokenIfPossible`
    // after AuthApiService has been registered.
    print('🟢 StorageService initialized (token validation deferred), user isLoggedIn: $isLoggedIn');
    return this;
  }

  /// Attach an [AuthApiService] after registration and optionally trigger
  /// token validation/refresh. Safe to call multiple times.
  Future<void> attachAuthApiService(AuthApiService service, {bool validateNow = true}) async {
    authApiService = service;
    if (validateNow) {
      await validateOrRefreshTokenIfPossible();
    }
  }

  /// Public helper that validates or refreshes token if [authApiService] is
  /// available. Returns whether refresh succeeded (or was not needed).
  Future<bool> validateOrRefreshTokenIfPossible() async {
    if (authApiService == null) {
      // Try resolving from GetIt as a fallback
      if (GetIt.I.isRegistered<AuthApiService>()) {
        authApiService = GetIt.I<AuthApiService>();
      } else {
        print('⚠️ AuthApiService not available yet; skipping token validation.');
        return false;
      }
    }
    await _validateOrRefreshToken();
    return true;
  }

  // ==============================================================
  // 🔐 USER CREDENTIALS
  // ==============================================================

  Future<void> saveUserCredentials(UserCredentials credentials) async {
    await _box.put(_keyUserCredentials, jsonEncode(credentials.toJson()));
    await _box.put(_keyAccessToken, credentials.accessToken);
    await _box.put(_keyRefreshToken, credentials.refreshToken);
    await _box.put(_keyWebSocketToken, credentials.websocketToken);
  }

  UserCredentials? getUserCredentials() {
    final data = _box.get(_keyUserCredentials);
    if (data != null) {
      return UserCredentials.fromJson(jsonDecode(data));
    }
    return null;
  }

  Future<void> setAccessToken(String token) async {
    await _box.put(_keyAccessToken, token);
  }

  Future<void> setRefreshToken(String token) async {
    await _box.put(_keyRefreshToken, token);
  }

  Future<void> setWebSocketToken(String token) async {
    await _box.put(_keyWebSocketToken, token);
  }

  String? getAccessToken() => _box.get(_keyAccessToken);

  String? getRefreshToken() => _box.get(_keyRefreshToken);

  String? getWebSocketToken() => _box.get(_keyWebSocketToken);

  /// Public method for external calls (used by Dio interceptors)
  Future<bool> attemptTokenRefresh() async {
    try {
      final api = authApiService ?? (GetIt.I.isRegistered<AuthApiService>() ? GetIt.I<AuthApiService>() : null);
      if (api == null) {
        print('⚠️ attemptTokenRefresh called but AuthApiService is unavailable.');
        return false;
      }
      final newResponse = await api.refreshAccessToken();

      if (newResponse != null &&
          newResponse.success &&
          newResponse.credentials != null) {
        await saveUserCredentials(newResponse.credentials!);
        print('🔄 Access token refreshed successfully (public call).');
        return true;
      } else {
        print('❌ Token refresh failed (public call).');
        await clearTokens();
        return false;
      }
    } catch (e) {
      print('🚨 attemptTokenRefresh() error: $e');
      return false;
    }
  }

  // ==============================================================
  // 🔁 AUTO REFRESH TOKEN
  // ==============================================================

  /// Checks token validity and auto-refreshes if close to expiry.
  Future<void> _validateOrRefreshToken() async {
    final token = getAccessToken();
    if (token == null || token.isEmpty) {
      print('🚫 No access token found.');
      return;
    }

    if (JwtDecoder.isExpired(token)) {
      print('⚠️ Token expired, refreshing...');
      await _attemptTokenRefresh();
    } else {
      // If token is valid but expiring soon (< 10 minutes), refresh proactively
      final expiryDate = JwtDecoder.getExpirationDate(token);
      final now = DateTime.now();
      final timeLeft = expiryDate.difference(now);

      if (timeLeft.inMinutes < 10) {
        print('⏳ Token expiring soon (${timeLeft.inMinutes} mins left), refreshing...');
        await _attemptTokenRefresh();
      } else {
        print('✅ Token still valid (${timeLeft.inMinutes} mins remaining).');
      }
    }
  }

  /// Requests new access & refresh tokens from backend.
  Future<void> _attemptTokenRefresh() async {
    final api = authApiService ?? (GetIt.I.isRegistered<AuthApiService>() ? GetIt.I<AuthApiService>() : null);
    if (api == null) {
      print('⚠️ _attemptTokenRefresh called but AuthApiService is unavailable. Clearing tokens.');
      await clearTokens();
      return;
    }

    final newResponse = await api.refreshAccessToken();

    if (newResponse != null && newResponse.success && newResponse.credentials != null) {
      await saveUserCredentials(newResponse.credentials!);
      print('🔄 Access token refreshed and stored.');
    } else {
      print('❌ Failed to refresh token, clearing stored tokens.');
      await clearTokens();
    }
  }

  bool get isLoggedIn {
    final token = getAccessToken();
    if (token == null || token.isEmpty) return false;
    return !JwtDecoder.isExpired(token);
  }

  Future<void> clearTokens() async {
    await _box.delete(_keyAccessToken);
    await _box.delete(_keyRefreshToken);
    await _box.delete(_keyWebSocketToken);
    await _box.delete(_keyUserCredentials);
  }

  // ==============================================================
  // 💾 SAVED LOGIN CREDENTIALS
  // ==============================================================

  Future<void> saveCredential(String username, String password) async {
    final existing = _savedCredentialsBoxRef.values
        .where((c) => c.username == username)
        .toList();

    if (existing.isEmpty) {
      final newCredential = SavedCredential(
        username: username,
        password: password,
        savedAt: DateTime.now(),
      );
      await _savedCredentialsBoxRef.add(newCredential);
    } else {
      final existingCredential = existing.first;
      existingCredential.savedAt = DateTime.now();
      await existingCredential.save();
    }
  }

  List<SavedCredential> getUserCredentialsList() {
    final creds = _savedCredentialsBoxRef.values.toList();
    creds.sort((a, b) => b.savedAt.compareTo(a.savedAt));
    return creds;
  }

  Future<void> removeCredential(String username) async {
    final entries = _savedCredentialsBoxRef.values
        .where((c) => c.username == username)
        .toList();
    for (var entry in entries) {
      await entry.delete();
    }
  }

  Future<void> clearAllCredentials() async => _savedCredentialsBoxRef.clear();

  Future<void> clearAll() async {
    await _box.clear();
    await _savedCredentialsBoxRef.clear();
  }
}