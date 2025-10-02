import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:dio/dio.dart';
import 'package:get/get.dart' as getx;
import 'package:image_picker/image_picker.dart';
import 'package:mobile/data/models/auth_response.dart';
import 'package:http_parser/http_parser.dart';
import 'package:mobile/data/models/login_request.dart';
import 'package:mobile/data/models/registration_request.dart';
import 'package:mobile/data/models/registration_response.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/data/models/application_submission_response.dart' as response_model;
import 'package:mobile/data/models/application_submission_request.dart';
import 'storage_service.dart';

class ApiService extends getx.GetxService {
  late Dio _dio;

  // Since you're using adb reverse, localhost should work
  static const String baseUrl = 'http://localhost:9001/api/v1';

  static ApiService get to => getx.Get.find();

  @override
  void onInit() {
    super.onInit();
    _initializeDio();
  }

  void _initializeDio() {
    _dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 15),
        sendTimeout: const Duration(seconds: 15),
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
      ),
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
          // Add JWT token to requests if available
          final token = StorageService.to.getToken();
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

  Future<AuthResponse> login(LoginRequest request) async {
    try {
      print('🚀 Attempting login to: $baseUrl/farmer/auth/login');

      final response = await _dio.post('/farmer/auth/login', data: request.toJson());

      print('✅ Login successful: ${response.statusCode}');
      final authResponse = AuthResponse.fromJson(response.data);

      // Store tokens if login is successful
      if (authResponse.success && authResponse.accessToken != null) {
        await StorageService.to.saveToken(authResponse.accessToken!);
      }
      if (authResponse.success && authResponse.refreshToken != null) {
        await StorageService.to.saveRefreshToken(authResponse.refreshToken!);
      }

      return authResponse;
    } on DioException catch (e) {
      print('❌ Login failed: ${e.message}');

      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout ||
          e.type == DioExceptionType.sendTimeout) {
        return AuthResponse(
          message: 'Connection timeout. Please check your network connection.',
          success: false,
        );
      } else if (e.type == DioExceptionType.connectionError) {
        return AuthResponse(
          message:
          'Cannot connect to server. Please ensure your backend is running and adb reverse is set up.',
          success: false,
        );
      } else if (e.response?.statusCode == 401 ||
          e.response?.statusCode == 400) {
        final errorData = e.response?.data;
        return AuthResponse(
          message: errorData['message'] ?? 'Invalid credentials',
          success: false,
        );
      } else {
        return AuthResponse(
          message:
          'Server error (${e.response?.statusCode}): ${e.response?.data}',
          success: false,
        );
      }
    } catch (e) {
      print('❌ Unexpected login error: $e');
      return AuthResponse(
        message: 'An unexpected error occurred: ${e.toString()}',
        success: false,
      );
    }
  }

  Future<RegistrationResponse> register(RegistrationRequest request) async {
    try {
      print('🚀 Attempting registration to: $baseUrl/farmers');

      final response = await _dio.post(
        '/farmer/auth/registration',
        data: request.toJson(),
      );

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
          message:
          'Cannot connect to server. Please ensure your backend is running and adb reverse is set up correctly.',
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

  // Add this method to get auth token
  String? getAuthToken() {
    return StorageService.to.getToken();
  }

  // Method to fetch application data - updated to handle direct array response
  Future<ApplicationContent?> fetchApplicationById(String id) async {
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

  Future<ApplicationResponse> fetchApplications() async {
    try {
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

  // New method to submit application form
  Future<response_model.ApplicationSubmissionResponse> submitApplicationForm(
      String applicationId,
      Map<String, dynamic> fieldValues,
      Map<String, XFile> files,
      ) async {
    try {
      print('🚀 Attempting to submit application form for ID: $applicationId');

      final formData = FormData();

      formData.fields.add(MapEntry("fieldValues", jsonEncode(fieldValues)));

      for (final entry in files.entries) {
        formData.files.add(
          MapEntry(
            entry.key, // field key, e.g. "crop_damage"
            await MultipartFile.fromFile(
              entry.value.path,
              filename: entry.value.name,
              contentType: MediaType("image", "jpeg"),
            ),
          ),
        );
      }
      print("FormData fields: ${formData.fields}");
      print(
        "FormData files: ${formData.files.map((e) => "${e.key}: ${e.value.filename}")}",
      );

      final response = await _dio.post(
        '/applications/$applicationId/submit',
        data: formData,
        options: Options(
          headers: {'Accept': 'application/json'},
          validateStatus:
              (status) =>
          status != null &&
              (status >= 200 && status < 300 || status == 400),
        ),
      );

      print('✅ Response: ${response.data}');

      return response_model.ApplicationSubmissionResponse.fromJson(response.data);
    } on DioException catch (e) {
      print('❌ DioException: ${e.response?.data}');
      return response_model.ApplicationSubmissionResponse(
        success: false,
        message: 'Submission failed',
        error: e.response?.data?.toString() ?? e.message,
      );
    }
  }

  Future<response_model.ApplicationSubmissionResponse> submitApplicationFormHttp(
      String applicationId,
      Map<String, dynamic> fieldValues,
      Map<String, XFile> files,
      ) async {
    final uri = Uri.parse(
      'http://localhost:8010/api/v1/applications/$applicationId/submit',
    );
    final request = http.MultipartRequest('POST', uri);

    // ✅ Add headers
    final token = StorageService.to.getToken();
    if (token != null && token.isNotEmpty) {
      request.headers['Authorization'] = 'Bearer $token';
    }
    request.headers['Accept'] = 'application/json';

    // ✅ Add JSON fieldValues
    request.fields['fieldValues'] = jsonEncode(fieldValues);

    // ✅ Add each file with correct content type
    for (final entry in files.entries) {
      final file = File(entry.value.path);
      request.files.add(
        http.MultipartFile(
          entry.key, // field key
          file.openRead(),
          await file.length(),
          filename: entry.value.name,
          contentType: MediaType(
            'image',
            'jpeg',
          ), // <- MUST use correct MediaType
        ),
      );
    }

    print('📦 Sending multipart/form-data request...');

    final streamedResponse = await request.send();
    final response = await http.Response.fromStream(streamedResponse);

    print('✅ Response status: ${response.statusCode}');
    print('✅ Response body: ${response.body}');

    if (response.statusCode == 200) {
      return response_model.ApplicationSubmissionResponse.fromJson(jsonDecode(response.body));
    } else {
      return response_model.ApplicationSubmissionResponse(
        success: false,
        message: 'Submission failed',
        error: response.body,
      );
    }
  }

  Future<response_model.ApplicationSubmissionResponse> submitApplication(
      ApplicationSubmissionRequest request,
      ) async {
    try {
      print('🚀 Submitting application for type: ${request.applicationTypeId}');
      print('📋 Field values: ${request.fieldValues}');
      print('📎 Document IDs: ${request.documentIds}');

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

      print('✅ Application submitted successfully: ${response.statusCode}');
      return response_model.ApplicationSubmissionResponse.fromJson(response.data);
    } on DioException catch (e) {
      print('❌ Application submission failed: ${e.message}');
      print('❌ Response data: ${e.response?.data}');

      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout ||
          e.type == DioExceptionType.sendTimeout) {
        return response_model.ApplicationSubmissionResponse(
          success: false,
          message: 'Connection timeout. Please try again.',
        );
      } else if (e.type == DioExceptionType.connectionError) {
        return response_model.ApplicationSubmissionResponse(
          success: false,
          message: 'Cannot connect to server. Please check your connection.',
        );
      } else if (e.response?.statusCode == 400) {
        final errorData = e.response?.data;
        return response_model.ApplicationSubmissionResponse(
          success: false,
          message: errorData['message'] ?? 'Invalid application data',
          error: errorData['errors'],
        );
      } else {
        return response_model.ApplicationSubmissionResponse(
          success: false,
          message: 'Server error (${e.response?.statusCode})',
        );
      }
    } catch (e) {
      print('❌ Unexpected error: $e');
      return response_model.ApplicationSubmissionResponse(
        success: false,
        message: 'An unexpected error occurred: ${e.toString()}',
      );
    }
  }

  Future<void> logout() async {
    // Remove access and refresh tokens from storage
    await StorageService.to.removeToken();
    await StorageService.to.removeRefreshToken();
    // ...add any other logout logic if needed...
  }
}
