import '../data/models/auth_response.dart';
import '../data/models/login_request.dart';
import '../data/services/api_service.dart';
import '../data/services/storage_service.dart';

class AuthManager {
  static Future<AuthResponse> login(
    String username,
    String password,
    bool rememberMe,
  ) async {
    final request = LoginRequest(
      username: username,
      password: password,
      rememberMe: rememberMe, // Pass the remember parameter to backend
    );
    final response = await ApiService.to.login(request);

    if (response.success && response.accessToken != null) {
      await StorageService.to.saveToken(response.accessToken!);
      await StorageService.to.saveRememberMe(rememberMe);

      if (rememberMe) {
        await StorageService.to.saveCredential(username, password);
      } else {
        await StorageService.to.clearAllCredentials();
      }
    }

    return response;
  }

  static Future<void> logout() async {
    await StorageService.to.removeToken();
    await StorageService.to.clearAllCredentials();
    await StorageService.to.saveRememberMe(false);
  }

  static Future<bool> isLoggedIn() async {
    final token = StorageService.to.getToken();
    return token != null && token.isNotEmpty;
  }

  static Future<Map<String, String?>> getSavedCredentials() async {
    final rememberMe = StorageService.to.getRememberMe();
    if (rememberMe) {
      return await StorageService.to.getCredentials();
    }
    return {'username': null, 'password': null};
  }
}
