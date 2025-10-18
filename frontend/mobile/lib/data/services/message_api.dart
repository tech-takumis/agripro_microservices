import 'package:dio/dio.dart';
import 'package:mobile/data/models/designated_response.dart';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/injection_container.dart'; // <-- Add this import

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
        final token = getIt<StorageService>().getAccessToken(); // <-- Use getIt here
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
    ));
  }

  // Find designated agriculture staff for chat
  Future<DesignatedResponse> findAgricultureDesignatedStaff() async {
    try {
      final response = await _dio.get('/communication/designated/agriculture/chat');

      print('Designated staff response: ${response.data}');
      return DesignatedResponse.fromJson(response.data);
    } on DioException catch (e) {
      print('Error finding designated staff: ${e.message}');
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

  // Handle Dio errors
  Exception _handleDioError(DioException e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return Exception('Connection timeout. Please check your internet connection.');
      case DioExceptionType.badResponse:
        final statusCode = e.response?.statusCode;
        final message = e.response?.data['message'] ?? 'Unknown error occurred';
        return Exception('Server error ($statusCode): $message');
      default:
        return Exception('An unexpected error occurred: ${e.message}');
    }
  }
}
