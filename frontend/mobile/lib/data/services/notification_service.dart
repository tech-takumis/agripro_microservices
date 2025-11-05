import 'package:mobile/data/models/application_notification.dart';
import 'package:mobile/data/services/notification_api.dart';
import '../../injection_container.dart';

class NotificationService {
  final NotificationApiService _apiService;

  NotificationService() : _apiService = getIt<NotificationApiService>();

  /// Create a new notification
  Future<ApplicationNotification> createNotification({
    required String recipientId,
    required String title,
    required String message,
  }) async {
    try {
      return await _apiService.createNotification(
        recipientId: recipientId,
        title: title,
        message: message,
      );
    } catch (e) {
      print('❌ [NotificationService] Create error: $e');
      rethrow;
    }
  }

  /// Fetch all notifications for a user
  Future<List<ApplicationNotification>> getNotificationsForUser(String recipientId) async {
    try {
      return await _apiService.getNotificationsForUser(recipientId);
    } catch (e) {
      print('❌ [NotificationService] Fetch error: $e');
      rethrow;
    }
  }

  /// Mark a notification as read
  Future<void> markNotificationAsRead(String notificationId) async {
    try {
      await _apiService.markNotificationAsRead(notificationId);
    } catch (e) {
      print('❌ [NotificationService] Mark as read error: $e');
      rethrow;
    }
  }

  /// Delete a notification
  Future<void> deleteNotification(String notificationId) async {
    try {
      await _apiService.deleteNotification(notificationId);
    } catch (e) {
      print('❌ [NotificationService] Delete error: $e');
      rethrow;
    }
  }
}