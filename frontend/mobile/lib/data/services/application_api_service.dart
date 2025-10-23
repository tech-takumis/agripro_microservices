import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:file_picker/file_picker.dart';
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/data/models/application_submission_request.dart';
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
      ApplicationSubmissionRequest request, {
      List<Map<String, dynamic>>? files,
      }) async {
    try {
      if (!(authState.isLoggedIn && authState.token != null && authState.token!.isNotEmpty)) {
        print('User not logged in, skipping submitApplication');
        return ApplicationSubmissionResponse(success: false, message: 'User not logged in');
      }
      print('🚀 Submitting application for type: ${request.applicationTypeId}');
      print('📋 Field values: ${request.fieldValues}');
      print('📎 Document IDs: ${request.documentIds}');
      print('📁 Files: ${files?.length ?? 0}');

      // Prepare multipart form data
      final formData = FormData();

      // Add the submission part as JSON string
      formData.fields.add(MapEntry(
        'submission',
        jsonEncode({
          'applicationTypeId': request.applicationTypeId,
          'fieldValues': request.fieldValues,
          'documentIds': [],
        }),
      ));

      // Add files if any, with correct content type
      if (files != null && files.isNotEmpty) {
        for (final fileMap in files) {
          final file = fileMap['file'] as PlatformFile;
          final fileName = fileMap['fileName'] as String;
          final mimeType = fileMap['mimeType'] as String;
          final mediaType = MediaType.parse(mimeType);

          MultipartFile multipartFile;
          if (file.path != null) {
            multipartFile = await MultipartFile.fromFile(
              file.path!,
              filename: fileName,
              contentType: mediaType,
            );
          } else if (file.bytes != null) {
            multipartFile = MultipartFile.fromBytes(
              file.bytes!,
              filename: fileName,
              contentType: mediaType,
            );
          } else {
            print('⚠️ Skipping file $fileName: no path or bytes.');
            continue;
          }

          // Only add file if not octet-stream (backend does not accept it)
          if (mimeType != 'application/octet-stream') {
            formData.files.add(MapEntry('files', multipartFile));
          } else {
            print('⚠️ Skipping file $fileName due to unsupported MIME type.');
          }
        }
      }

      final response = await _dio.post(
        '/applications/submit',
        data: formData,
        options: Options(
          headers: {
            'Content-Type': 'multipart/form-data',
            'Accept': 'application/json',
          },
        ),
      );

      print('✅ Application submitted successfully: ${response.statusCode}');
      return ApplicationSubmissionResponse.fromJson(response.data);
    } on DioException catch (e) {
      print('❌ Application submission failed: ${e.message}');
      print('❌ Response data: ${e.response?.data}');

      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout ||
          e.type == DioExceptionType.sendTimeout) {
        return ApplicationSubmissionResponse(
          success: false,
          message: 'Connection timeout. Please try again.',
        );
      } else if (e.type == DioExceptionType.connectionError) {
        return ApplicationSubmissionResponse(
          success: false,
          message: 'Cannot connect to server. Please check your connection.',
        );
      } else if (e.response?.statusCode == 400) {
        final errorData = e.response?.data;
        return ApplicationSubmissionResponse(
          success: false,
          message: errorData['message'] ?? 'Invalid application data',
          errors: errorData['errors'],
        );
      } else {
        return ApplicationSubmissionResponse(
          success: false,
          message: 'Server error (${e.response?.statusCode})',
        );
      }
    } catch (e) {
      print('❌ Unexpected error: $e');
      return ApplicationSubmissionResponse(
        success: false,
        message: 'An unexpected error occurred: ${e.toString()}',
      );
    }
  }
}
