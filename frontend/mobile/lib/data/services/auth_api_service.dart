import 'package:dio/dio.dart';
import 'package:mobile/data/models/auth_response.dart';
import 'package:mobile/data/models/login_request.dart';
import 'package:mobile/data/models/registration_request.dart';
import 'package:mobile/data/models/registration_response.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/data/models/user_credentials.dart';

class AuthApiService {
  final Dio _dio;
  final String baseUrl;
  final StorageService storageService; 

  AuthApiService(this._dio, {required this.baseUrl, required this.storageService}) {
    _initializeDio();
  }

  void _initializeDio() {
    _dio.options = BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 15),
      sendTimeout: const Duration(seconds: 15),
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    );

    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = storageService.getAccessToken();
          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          return handler.next(options);
        },
        onError: (error, handler) async {
          if (error.response?.statusCode == 401) {
            final requestOptions = error.requestOptions;
            if (requestOptions.path.contains('/auth/refresh')) {
              print('🚫 Refresh endpoint itself failed.');
              return handler.next(error);
            }

            print('🔁 Token expired — attempting to refresh...');
            final newTokens = await storageService.attemptTokenRefresh();
            if (newTokens) {
              final newToken = storageService.getAccessToken()!;
              final cloneReq = await _retryRequest(requestOptions, newToken);
              return handler.resolve(cloneReq);
            } else {
              print('❌ Refresh token expired — forcing logout');
              await storageService.clearAll();
              // Navigate to login (handled by controller, not here)
              return handler.next(error);
            }
          }
          return handler.next(error);
        },
      ),
    );

    _dio.interceptors.add(
      LogInterceptor(
        requestBody: true,
        responseBody: true,
        requestHeader: true,
        responseHeader: false,
        error: true,
        logPrint: (obj) => print('🌐 API: $obj'),
      ),
    );
  }

  Future<Response> _retryRequest(RequestOptions requestOptions, String newToken) async {
    final newOptions = Options(
      method: requestOptions.method,
      headers: {
        ...requestOptions.headers,
        'Authorization': 'Bearer $newToken',
      },
    );

    print('🔁 Retrying request to ${requestOptions.path}...');
    final cloneReq = await _dio.request(
      requestOptions.path,
      data: requestOptions.data,
      queryParameters: requestOptions.queryParameters,
      options: newOptions,
    );
    return cloneReq;
  }

  Future<UserCredentials?> login(LoginRequest request) async {
    try {
      print('🚀 [DEBUG] AuthApiService baseUrl: $baseUrl');
      print('🚀 Attempting login to: $baseUrl/farmer/auth/login');
      print('Login request data: ${request.toJson()}');
      final response = await _dio.post('/farmer/auth/login', data: request.toJson());
      print('✅ Login successful: ${response.statusCode}');
      return UserCredentials.fromJson(response.data);
    } on DioException catch (e) {
      print('❌ Login failed: ${e.message}');
      // Return null for all error cases to match the return type
      return null;
    } catch (e) {
      print('❌ Unexpected login error: $e');
      return null;
    }
  }


  Future<AuthResponse?> refreshAccessToken() async {
    final refreshToken = storageService.getRefreshToken();
    if (refreshToken == null || refreshToken.isEmpty) {
      print('⚠️ No refresh token available.');
      return null;
    }

    try {
      print('🔁 Attempting token refresh...');
      final response = await _dio.post(
        '/farmer/auth/refresh',
        data: {'refreshToken': refreshToken},
      );

      if (response.statusCode == 200 && response.data['success'] == true) {
        print('✅ Token refreshed successfully.');
        return AuthResponse.fromJson(response.data);
      } else {
        print('❌ Token refresh failed: ${response.data}');
        return null;
      }
    } on DioException catch (e) {
      print('🚨 Refresh token request failed: ${e.message}');
      return null;
    }
  }

  Future<RegistrationResponse> register(RegistrationRequest request) async {
    try {
      print('🚀 Attempting registration to: $baseUrl/farmers');
      final response = await _dio.post('/farmer/auth/registration', data: request.toJson());
      print('✅ Registration successful: ${response.statusCode}');
      return RegistrationResponse.fromJson(response.data);
    } on DioException catch (e) {
      print('❌ Registration failed: ${e.message}');
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout ||
          e.type == DioExceptionType.sendTimeout) {
        return RegistrationResponse(
          success: false,
          error: 'Timeout Error',
          message: 'Connection timeout. Please try again.',
        );
      } else if (e.type == DioExceptionType.connectionError) {
        return RegistrationResponse(
          success: false,
          error: 'Connection Error',
          message: 'Cannot connect to server. Please ensure your backend is running.',
        );
      } else if (e.response?.statusCode == 400) {
        final errorData = e.response?.data;
        return RegistrationResponse.fromJson(errorData);
      } else {
        return RegistrationResponse(
          success: false,
          error: 'Server Error',
          message: 'Server error (${e.response?.statusCode})',
        );
      }
    } catch (e) {
      print('❌ Unexpected registration error: $e');
      return RegistrationResponse(
        success: false,
        error: 'Unexpected Error',
        message: 'An unexpected error occurred: ${e.toString()}',
      );
    }
  }

  Future<void> logout() async {
      try {
        final response = await _dio.post(
            '/farmer/auth/logout',
          options: Options(responseType: ResponseType.plain)
        );

        print('✅ Backend logout response: ${response.statusCode}');
      } catch (e) {
        print('❌ Backend logout failed: $e');
        // Optionally handle backend logout failure
      }
  }
}