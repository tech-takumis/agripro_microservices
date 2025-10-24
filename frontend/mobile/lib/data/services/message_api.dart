import 'package:dio/dio.dart';
import 'package:mobile/data/models/designated_response.dart';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/injection_container.dart'; 
import 'dart:convert';
import 'package:mobile/data/models/message_request_dto.dart';
import 'package:http_parser/http_parser.dart';

class MessageApi {
  static final MessageApi _instance = MessageApi._internal();
  factory MessageApi() => _instance;

  final Dio _dio = Dio(BaseOptions(
    baseUrl: 'http://localhost:9001/api/v1',
    connectTimeout: const Duration(seconds: 5),
    receiveTimeout: const Duration(seconds: 3),
  ));

  MessageApi._internal() {
    // Add auth interceptor
    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) {
        final token = getIt<StorageService>().getAccessToken();
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
    ));
    // Add logging interceptor for debugging
    _dio.interceptors.add(LogInterceptor(
      request: true,
      requestHeader: true,
      requestBody: true,
      responseHeader: true,
      responseBody: true,
      error: true,
      logPrint: (obj) => print('[DioLog] $obj'),
    ));
  }

  // Find designated agriculture staff for chat
  Future<DesignatedResponse> findAgricultureDesignatedStaff() async {
    try {
      final response = await _dio.get('/agriculture/designated/type/FARMER_AGRICULTURE_COMMUNICATION');

      print('Designated staff response: \\${response.data}');
      return DesignatedResponse.fromJson(response.data);
    } on DioException catch (e) {
      print('Error finding designated staff: \\${e.message}');
      throw _handleDioError(e);
    }
  }

  // Get all messages with agriculture staff
  Future<List<Message>> getMessagesWithAgricultureStaff(String farmerId) async {
    try {
      final response = await _dio.get('/chat/$farmerId/messages');

      if (response.data is! List) {
        throw Exception('Invalid response format: expected a list of messages');
      }

      return (response.data as List)
          .map((json) => Message.fromJson(json))
          .toList();
    } on DioException catch (e) {
      print('Error getting messages: ${e.message}');
      throw _handleDioError(e);
    } catch (e) {
      print('Error parsing messages: $e');
      throw Exception('Failed to parse message data: $e');
    }
  }

  // Create a new message (multipart/form-data)
  Future<Message> createMessage({
    required MessageRequestDto messageRequest,
    List<MultipartFile>? attachments,
  }) async {
    try {
      final formData = FormData();
      final messageJson = jsonEncode(messageRequest.toJson());
      print('[MessageApi] Sending message: $messageJson');
      // Instead of formData.fields.add, use MultipartFile.fromString for message part
      formData.files.add(MapEntry(
        'message',
        MultipartFile.fromString(
          messageJson,
          contentType: MediaType('application', 'json'),
          filename: 'blob', // Some servers require a filename
        ),
      ));
      if (attachments != null && attachments.isNotEmpty) {
        for (final file in attachments) {
          formData.files.add(MapEntry(
            'attachments',
            file,
          ));
        }
      }
      final response = await _dio.post(
        '/chat',
        data: formData,
      );
      print('[MessageApi] Response status: \\${response.statusCode}, data: \\${response.data}');
      if (response.data is Map<String, dynamic>) {
        return Message.fromJson(response.data);
      } else if (response.data is String) {
        // Try to decode if backend returns a string
        return Message.fromJson(jsonDecode(response.data));
      } else {
        throw Exception('Unexpected response type: \\${response.data.runtimeType}');
      }
    } on DioException catch (e) {
      print('Error creating message: \\${e.message}');
      throw _handleDioError(e);
    }
  }

  // Handle Dio errors
  Exception _handleDioError(DioException e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return Exception('Connection timeout. Please check your internet connection.');
      case DioExceptionType.badResponse:
        final statusCode = e.response?.statusCode;
        dynamic data = e.response?.data;
        String message = 'Unknown error occurred';
        if (data is Map && data['message'] != null) {
          message = data['message'].toString();
        } else if (data is String) {
          message = data;
        }
        return Exception('Server error ($statusCode): $message');
      default:
        return Exception('An unexpected error occurred: ${e.message}');
    }
  }
}
