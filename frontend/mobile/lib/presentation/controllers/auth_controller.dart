import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:mobile/data/models/login_request.dart';
import 'package:mobile/data/services/auth_api_service.dart';
import 'package:mobile/data/services/location_service.dart';
import 'package:mobile/data/services/message_service.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/features/messages/providers/message_provider.dart';

import '../../data/services/websocket.dart';
import '../../injection_container.dart';

class AuthState {
  final bool isLoading;
  final bool isLoggedIn;
  final String errorMessage;
  final String userName;
  final String userEmail;
  final String? token;
  final String? userId;

  AuthState({
    this.isLoading = false,
    this.isLoggedIn = false,
    this.errorMessage = '',
    this.userName = '',
    this.userEmail = '',
    this.token,
    this.userId,
  });

  AuthState copyWith({
    bool? isLoading,
    bool? isLoggedIn,
    String? errorMessage,
    String? userName,
    String? userEmail,
    String? token,
    String? userId,
  }) {
    return AuthState(
      isLoading: isLoading ?? this.isLoading,
      isLoggedIn: isLoggedIn ?? this.isLoggedIn,
      errorMessage: errorMessage ?? this.errorMessage,
      userName: userName ?? this.userName,
      userEmail: userEmail ?? this.userEmail,
      token: token ?? this.token,
      userId: userId ?? this.userId,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  final Ref ref;
  final AuthApiService authApiService;
  final StorageService storageService;
  final MessageService messageService;
  final LocationService locationService;
  final WebSocketService webSocketService;

  AuthNotifier({
    required this.ref,
    required this.authApiService,
    required this.storageService,
    required this.messageService,
    required this.locationService,
    required this.webSocketService,
  }) : super(AuthState()) {
    _checkLoginStatus();
  }

  void _checkLoginStatus() {
    final token = storageService.getAccessToken();
    print('🧩 Token on startup: $token');
    if (token != null && token.isNotEmpty && !JwtDecoder.isExpired(token)) {
      final credentials = storageService.getUserCredentials();
      state = state.copyWith(
        isLoggedIn: true,
        userName: credentials != null
            ? "${credentials.user.firstName} ${credentials.user.lastName}"
            : '',
        userEmail: credentials?.user.email ?? '',
        token: token,
        userId: credentials?.id,
      );
    } else {
      state = state.copyWith(isLoggedIn: false);
    }
  }

  Future<void> login(String username, String password, bool rememberMe) async {
    try {
      state = state.copyWith(isLoading: true, errorMessage: '');
      final request = LoginRequest(
        username: username,
        password: password,
        rememberMe: rememberMe,
      );

      final credentials = await authApiService.login(request);
      print("Login user credentials response: $credentials");
      if (credentials != null) {
        // Save credentials once (fixes redundant saves)
        await storageService.saveUserCredentials(credentials);

        // Handle "remember me" functionality
        if (rememberMe) {
          await storageService.saveCredential(username, password);
          print('✅ Saved username and password for autofill');
        }

        // Initialize message service
        await messageService.init(
          token: credentials.accessToken,
          userId: credentials.id,
        );

        // Invalidate the provider so widgets get the updated MessageService
        ref.invalidate(messageServiceProvider);

        state = state.copyWith(
          isLoading: false,
          isLoggedIn: true,
          userName: "${credentials.user.firstName} ${credentials.user.lastName}",
          userEmail: credentials.user.email,
          token: credentials.accessToken,
          userId: credentials.id,
        );

        // Navigate to home (use GoRouter or similar, not GetX)
        getIt<GoRouter>().go('/home');
        await _handlePostLoginLocationCheck();
      } else {
        state = state.copyWith(
          isLoading: false,
          errorMessage: 'Login failed',
        );
      }
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        errorMessage: 'An unexpected error occurred: {e.toString()}',
      );
    }
  }

  Future<void> _handlePostLoginLocationCheck() async {
    final locationReady = await locationService.checkAndRequestLocationReadiness();
    if (!locationReady) {
      // Trigger dialog via state or UI (handled in LoginPage)
      state = state.copyWith(errorMessage: 'Location services required');
    }
  }

  Future<void> logout() async {
    try {
      webSocketService.disconnect();
      await storageService.clearAll();
      state = state.copyWith(
        isLoggedIn: false,
        userName: '',
        userEmail: '',
        token: null,
        userId: null,
        errorMessage: '',
      );
      getIt<GoRouter>().go('/login');
    } catch (e) {
      print('Logout error: $e');
      state = state.copyWith(errorMessage: 'Logout failed: $e');
    }
  }

  void clearError() {
    state = state.copyWith(errorMessage: '');
  }
}

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier(
    ref: ref,
    authApiService: getIt<AuthApiService>(),
    storageService: getIt<StorageService>(),
    messageService: getIt<MessageService>(),
    locationService: getIt<LocationService>(),
    webSocketService: getIt<WebSocketService>(),
  );
});