import 'package:flutter/material.dart'; // Import for AlertDialog
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import '../data/models/login_request.dart';
import '../data/services/api_service.dart';
import '../data/services/storage_service.dart';
import '../data/services/location_service.dart'; // Import LocationService

class AuthController extends GetxController {
  final _isLoading = false.obs;
  final _isLoggedIn = false.obs;
  final _errorMessage = ''.obs;

  bool get isLoading => _isLoading.value;
  bool get isLoggedIn => _isLoggedIn.value;
  String get errorMessage => _errorMessage.value;

  @override
  void onInit() {
    super.onInit();
    _checkLoginStatus();
    // Initialize LocationService
    Get.put(LocationService());
  }

  void _checkLoginStatus() {
    final token = StorageService.to.getToken();
    _isLoggedIn.value = token != null && token.isNotEmpty;
  }

  Future<void> login(String username, String password, bool rememberMe) async {
    try {
      _isLoading.value = true;
      _errorMessage.value = '';

      final request = LoginRequest(
        username: username,
        password: password,
        rememberMe: rememberMe,
      );

      final response = await ApiService.to.login(request);
      if (response.success && response.accessToken != null) {
        await StorageService.to.saveToken(response.accessToken!);
        if (response.refreshToken != null) {
          await StorageService.to.saveRefreshToken(response.refreshToken!);
        }
        await StorageService.to.saveRememberMe(rememberMe);
        if (rememberMe) {
          await StorageService.to.saveCredential(username, password);
        }
        _isLoggedIn.value = true;
        Get.offAllNamed('/home'); // This should work now
        _handlePostLoginLocationCheck();
      } else {
        _errorMessage.value = response.message;
      }
    } catch (e) {
      _errorMessage.value = 'An unexpected error occurred: ${e.toString()}';
    } finally {
      _isLoading.value = false;
    }
  }

  Future<void> _handlePostLoginLocationCheck() async {
    final locationReady =
        await LocationService.to.checkAndRequestLocationReadiness();
    if (!locationReady) {
      _showLocationPromptDialog();
    }
  }

  void _showLocationPromptDialog() {
    Get.dialog(
      AlertDialog(
        title: const Text('Location Services Required'),
        content: const Text(
          'To automatically capture GPS coordinates for your application forms, please enable location services and grant permissions for this app in your device settings.',
        ),
        actions: [
          TextButton(
            onPressed: () {
              Get.back(); // Close dialog
            },
            child: const Text('Later'),
          ),
          TextButton(
            onPressed: () async {
              Get.back(); // Close dialog
              // Attempt to open location settings or app settings
              bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
              if (!serviceEnabled) {
                await LocationService.to.openLocationSettings();
              } else {
                await LocationService.to.openAppSettings();
              }
            },
            child: const Text('Open Settings'),
          ),
        ],
      ),
      barrierDismissible: false, // User must interact with the dialog
    );
  }

  Future<void> logout() async {
    try {
      await StorageService.to.removeToken();
      await StorageService.to.saveRememberMe(false);
      _isLoggedIn.value = false;
      Get.offAllNamed('/login');
    } catch (e) {
      print('Logout error: $e');
    }
  }

  void clearError() {
    _errorMessage.value = '';
  }
}
