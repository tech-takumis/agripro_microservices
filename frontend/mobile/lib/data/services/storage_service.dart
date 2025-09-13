import 'package:hive_flutter/hive_flutter.dart';
import 'package:get/get.dart';
import '../models/saved_credential.dart';

class StorageService extends GetxService {
  static const String _authBoxName = 'auth_box';
  static const String _credentialsBoxName = 'credentials_box';
  static const String _tokenKey = 'auth_token';
  static const String _rememberMeKey = 'remember_me';
  static const String _refreshTokenKey = 'refresh_token';

  late Box _authBox;
  late Box<SavedCredential> _credentialsBox;

  static StorageService get to => Get.find();

  Future<StorageService> init() async {
    await Hive.initFlutter();

    // Register adapters
    if (!Hive.isAdapterRegistered(0)) {
      Hive.registerAdapter(SavedCredentialAdapter());
    }

    // Open boxes
    _authBox = await Hive.openBox(_authBoxName);
    _credentialsBox = await Hive.openBox<SavedCredential>(_credentialsBoxName);

    return this;
  }

  // Token management
  Future<void> saveToken(String token) async {
    await _authBox.put(_tokenKey, token);
  }

  String? getToken() {
    return _authBox.get(_tokenKey);
  }

  Future<void> removeToken() async {
    await _authBox.delete(_tokenKey);
  }

  // Refresh Token management
  Future<void> saveRefreshToken(String token) async {
    await _authBox.put(_refreshTokenKey, token);
  }

  String? getRefreshToken() {
    return _authBox.get(_refreshTokenKey);
  }

  Future<void> removeRefreshToken() async {
    await _authBox.delete(_refreshTokenKey);
  }

  // Remember me management
  Future<void> saveRememberMe(bool remember) async {
    await _authBox.put(_rememberMeKey, remember);
  }

  bool getRememberMe() {
    return _authBox.get(_rememberMeKey, defaultValue: false);
  }

  // Credentials management
  Future<void> saveCredential(String username, String password) async {
    // Check if credential already exists
    final existingIndex = _credentialsBox.values.toList().indexWhere(
      (cred) => cred.username == username,
    );

    final credential = SavedCredential(
      username: username,
      password: password,
      savedAt: DateTime.now(),
    );

    if (existingIndex != -1) {
      // Update existing credential
      await _credentialsBox.putAt(existingIndex, credential);
    } else {
      // Add new credential
      await _credentialsBox.add(credential);
    }
  }

  List<SavedCredential> getSavedCredentials() {
    return _credentialsBox.values.toList()
      ..sort((a, b) => b.savedAt.compareTo(a.savedAt)); // Most recent first
  }

  Future<Map<String, String?>> getCredentials() async {
    final credentials = getSavedCredentials();
    if (credentials.isNotEmpty) {
      final latest = credentials.first;
      return {'username': latest.username, 'password': latest.password};
    }
    return {'username': null, 'password': null};
  }

  Future<void> removeCredential(String username) async {
    final key = _credentialsBox.keys.firstWhere(
      (key) => _credentialsBox.get(key)?.username == username,
      orElse: () => null,
    );

    if (key != null) {
      await _credentialsBox.delete(key);
    }
  }

  Future<void> clearAllCredentials() async {
    await _credentialsBox.clear();
  }

  Future<void> clearAll() async {
    await _authBox.clear();
    await _credentialsBox.clear();
  }
}
