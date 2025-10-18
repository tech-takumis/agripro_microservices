import 'dart:convert';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/data/models/application_submission_request.dart';
import 'package:mobile/data/models/application_submission_response.dart' as response_model;

import 'storage_service.dart';
import 'package:mobile/injection_container.dart'; // For getIt

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
    final token = getIt<StorageService>().getAccessToken();
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
}
