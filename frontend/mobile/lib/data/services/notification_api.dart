import 'package:dio/dio.dart';
import 'package:mobile/data/models/application_notification.dart';
import 'package:mobile/data/models/api_error_response.dart';

class NotificationApiService {
  final Dio _dio;
  final String baseUrl;

  NotificationApiService(
      this._dio, {
        required this.baseUrl,
      }) {
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
  }

  Future<dynamic> _handleError(DioException e, String operation) {
    print('❌ [NotificationAPI] $operation error: ${e.message}');
    if (e.response?.data != null) {
      final errorResponse = ApiErrorResponse.fromJson(e.response!.data);
      throw errorResponse;
    }
    throw ApiErrorResponse(
      success: false,
      message: e.message ?? 'An unknown error occurred',
      status: e.response?.statusCode ?? 500,
      timestamp: DateTime.now(),
    );
  }

  /// Create a new notification
  /// POST /api/v1/notifications
  Future<ApplicationNotification> createNotification({
    required String recipientId,
    required String title,
    required String message,
  }) async {
    try {
      final response = await _dio.post(
        '/notifications',
        data: {
          'recipientId': recipientId,
          'title': title,
          'message': message,
        },
      );

      if (response.statusCode == 200) {
        return ApplicationNotification.fromJson(response.data);
      } else {
        throw ApiErrorResponse(
          success: false,
          message: 'Failed to create notification',
          status: response.statusCode ?? 500,
          timestamp: DateTime.now(),
        );
      }
    } on DioException catch (e) {
      throw await _handleError(e, 'Create') as ApiErrorResponse;
    }
  }

  /// Get all notifications for a specific user
  /// GET /api/v1/notifications/{recipientId}
  Future<List<ApplicationNotification>> getNotificationsForUser(String recipientId) async {
    try {
      final response = await _dio.get('/notifications/$recipientId');

      if (response.statusCode == 200) {
        final List<dynamic> data = response.data;
        return data.map((json) => ApplicationNotification.fromJson(json)).toList();
      } else {
        throw ApiErrorResponse(
          success: false,
          message: 'Failed to fetch notifications',
          status: response.statusCode ?? 500,
          timestamp: DateTime.now(),
        );
      }
    } on DioException catch (e) {
      throw await _handleError(e, 'Fetch') as ApiErrorResponse;
    }
  }

  /// Mark a notification as read
  /// PUT /api/v1/notifications/{notificationId}/read
  Future<void> markNotificationAsRead(String notificationId) async {
    try {
      final response = await _dio.put('/notifications/$notificationId/read');

      if (response.statusCode != 200) {
        throw ApiErrorResponse(
          success: false,
          message: 'Failed to mark notification as read',
          status: response.statusCode ?? 500,
          timestamp: DateTime.now(),
        );
      }
    } on DioException catch (e) {
      throw await _handleError(e, 'Mark as read');
    }
  }

  /// Delete a notification
  /// DELETE /api/v1/notifications/{notificationId}
  Future<void> deleteNotification(String notificationId) async {
    try {
      final response = await _dio.delete('/notifications/$notificationId');

      if (response.statusCode != 204) {
        throw ApiErrorResponse(
          success: false,
          message: 'Failed to delete notification',
          status: response.statusCode ?? 500,
          timestamp: DateTime.now(),
        );
      }
    } on DioException catch (e) {
      throw await _handleError(e, 'Delete');
    }
  }
}