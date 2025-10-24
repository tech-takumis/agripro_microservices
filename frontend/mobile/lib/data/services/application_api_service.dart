import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/data/models/application_submission.dart';
import 'package:mobile/injection_container.dart'; // For getIt
import '../../presentation/controllers/auth_controller.dart';
import 'storage_service.dart';

class ApplicationApiService {
  final Dio _dio;
  final String baseUrl;

  ApplicationApiService(this._dio, {required this.baseUrl}) {
    _dio.options = BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
    );

    // Add interceptors for logging and authentication
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

    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = getIt<StorageService>().getAccessToken();
          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
            print(
              '🌐 API: Adding Authorization header: Bearer ...${token.substring(token.length - 20)}',
            );
          }
          return handler.next(options);
        },
        onError: (error, handler) {
          print('🚨 API Error: ${error.message}');
          print('🚨 API Error Type: ${error.type}');
          print('🚨 API Error Response: ${error.response?.data}');
          print('🚨 API Error Stack: ${error.stackTrace}');
          handler.next(error);
        },
      ),
    );
  }

  // Method to fetch application data - updated to handle direct array response
  Future<ApplicationContent?> fetchApplicationById(String id,AuthState authState) async {
    if (!(authState.isLoggedIn && authState.token != null && authState.token!.isNotEmpty)) {
      print('User not logged in, skipping fetchApplications');
      return null;
    }
    try {
      print('🚀 Fetching application type: $id');
      final response = await _dio.get('/application/types/$id');
      print('✅ Application type fetched successfully: ${response.statusCode}');
      return ApplicationContent.fromJson(response.data);
    } on DioException catch (e) {
      print('❌ Fetch application type failed: ${e.message}');
      return null;
    } catch (e) {
      print('❌ Unexpected error: $e');
      return null;
    }
  }

  Future<ApplicationResponse?> fetchApplications(AuthState authState) async {
    try {
      if (!(authState.isLoggedIn && authState.token != null && authState.token!.isNotEmpty)) {
        print('User not logged in, skipping fetchApplications');
        return null;
      }
      print('🚀 [DEBUG] ApplicationApiService baseUrl: $baseUrl');
      print('🚀 Fetching applications from: $baseUrl/application/types');
      final response = await _dio.get('/application/types');
      print('✅ Applications fetched successfully: ${response.statusCode}');
      
      // API returns a direct array
      final List<dynamic> data = response.data as List<dynamic>;
      return ApplicationResponse(
        statusCode: response.statusCode ?? 200,
        message: 'Success',
        content: data.map((e) => ApplicationContent.fromJson(e)).toList(),
      );
    } catch (e) {
      print('❌ Fetch applications failed: $e');
      throw Exception('Failed to fetch applications');
    }
  }

  Future<ApplicationSubmissionResponse> submitApplication(
      AuthState authState,
      ApplicationSubmissionRequest request) async {
    try {
      if (!(authState.isLoggedIn && authState.token != null && authState.token!.isNotEmpty)) {
        print('User not logged in, skipping submitApplication');
        return ApplicationSubmissionResponse(success: false, message: 'User not logged in', applicationId: '');
      }
      print('🚀 Submitting application for type: \\${request.applicationTypeId}');
      print('📋 Field values: \\${request.fieldValues}');
      print('📎 Document IDs: \\${request.documentIds}');

      // Send as application/json
      final response = await _dio.post(
        '/applications/submit',
        data: request.toJson(),
        options: Options(
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
        ),
      );

      print('✅ Application submitted successfully: \\${response.statusCode}');
      if (response.data is Map<String, dynamic> || response.data is Map) {
        return ApplicationSubmissionResponse.fromJson(response.data);
      } else if (response.data is String) {
        // Try to decode if backend returns a stringified JSON
        try {
          final Map<String, dynamic> json = jsonDecode(response.data);
          return ApplicationSubmissionResponse.fromJson(json);
        } catch (_) {
          return ApplicationSubmissionResponse(success: false, message: response.data.toString(), applicationId: '');
        }
      } else {
        return ApplicationSubmissionResponse(success: false, message: 'Unknown response format', applicationId: '');
      }
    } on DioException catch (e) {
      print('❌ Application submission failed: \\${e.message}');
      print('❌ Response data: \\${e.response?.data}');
      if (e.response?.data is Map<String, dynamic> || e.response?.data is Map) {
        return ApplicationSubmissionResponse.fromJson(e.response?.data);
      } else if (e.response?.data is String) {
        try {
          final Map<String, dynamic> json = jsonDecode(e.response?.data);
          return ApplicationSubmissionResponse.fromJson(json);
        } catch (_) {
          return ApplicationSubmissionResponse(success: false, message: (e.response?.data?.toString() ?? ''), applicationId: '');
        }
      }
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout ||
          e.type == DioExceptionType.sendTimeout) {
        return ApplicationSubmissionResponse(success: false, message: 'Connection timeout. Please try again.', applicationId: '');
      } else if (e.type == DioExceptionType.connectionError) {
        return ApplicationSubmissionResponse(success: false, message: 'Cannot connect to server. Please check your connection.', applicationId: '');
      }
      return ApplicationSubmissionResponse(success: false, message: 'Failed to submit application', applicationId: '');
    } catch (e) {
      print('❌ Unexpected error: \\${e.toString()}');
      return ApplicationSubmissionResponse(success: false, message: 'Unexpected error: \\${e.toString()}', applicationId: '');
    }
  }
}
