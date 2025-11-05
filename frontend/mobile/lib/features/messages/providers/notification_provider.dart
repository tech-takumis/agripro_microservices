import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/application_notification.dart';
import 'package:mobile/data/models/api_error_response.dart';
import 'package:mobile/data/services/notification_service.dart';
import 'package:mobile/data/services/websocket.dart';
import '../../../injection_container.dart';

class NotificationState {
  final List<ApplicationNotification> notifications;
  final bool isLoading;
  final ApiErrorResponse? error;

  NotificationState({
    this.notifications = const [],
    this.isLoading = false,
    this.error,
  });

  int get unreadCount => notifications.where((n) => !n.read).length;

  NotificationState copyWith({
    List<ApplicationNotification>? notifications,
    bool? isLoading,
    ApiErrorResponse? error,
  }) {
    return NotificationState(
      notifications: notifications ?? this.notifications,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class NotificationNotifier extends StateNotifier<NotificationState> {
  final NotificationService _notificationService;
  final WebSocketService _webSocketService;

  NotificationNotifier()
      : _notificationService = getIt<NotificationService>(),
        _webSocketService = getIt<WebSocketService>(),
        super(NotificationState()) {
    _initializeWebSocketListener();
  }

  /// Initialize WebSocket listener for real-time notifications
  void _initializeWebSocketListener() {
    _webSocketService.addListener(_handleWebSocketNotification);
    print('✅ [NotificationProvider] WebSocket listener initialized');
  }

  /// Handle incoming WebSocket notifications
  void _handleWebSocketNotification(Map<String, dynamic> data) {
    // Check if this is a notification (has 'title' key) vs a message
    if (data.containsKey('title')) {
      try {
        final notification = ApplicationNotification.fromJson(data);
        _addNotificationToList(notification);
        print('🔔 [NotificationProvider] Received real-time notification: ${notification.title}');
      } catch (e) {
        print('❌ [NotificationProvider] Failed to parse notification: $e');
      }
    }
  }

  /// Add a notification to the list (prepend to show newest first)
  void _addNotificationToList(ApplicationNotification notification) {
    final notifications = [...state.notifications];
    
    // Check if notification already exists (avoid duplicates)
    final existingIndex = notifications.indexWhere((n) => n.id == notification.id);
    if (existingIndex != -1) {
      notifications[existingIndex] = notification;
    } else {
      notifications.insert(0, notification);
    }
    
    state = state.copyWith(notifications: notifications);
  }

  /// Fetch all notifications for a user
  Future<void> fetchNotifications(String recipientId) async {
    if (state.isLoading) return;

    state = state.copyWith(isLoading: true, error: null);

    try {
      final fetchedNotifications = await _notificationService.getNotificationsForUser(recipientId);
      fetchedNotifications.sort((a, b) => b.time.compareTo(a.time)); // Sort by time (newest first)
      state = state.copyWith(notifications: fetchedNotifications, isLoading: false);
      print('✅ [NotificationProvider] Fetched ${fetchedNotifications.length} notifications');
    } catch (e) {
      state = state.copyWith(
        error: e is ApiErrorResponse ? e : null,
        isLoading: false,
      );
      print('❌ [NotificationProvider] Fetch error: $e');
      rethrow;
    }
  }

  /// Create a new notification
  Future<void> createNotification({
    required String recipientId,
    required String title,
    required String message,
  }) async {
    try {
      final notification = await _notificationService.createNotification(
        recipientId: recipientId,
        title: title,
        message: message,
      );
      _addNotificationToList(notification);
      print('✅ [NotificationProvider] Created notification: ${notification.title}');
    } catch (e) {
      print('❌ [NotificationProvider] Create error: $e');
      rethrow;
    }
  }

  /// Mark a notification as read
  Future<void> markAsRead(String notificationId) async {
    try {
      await _notificationService.markNotificationAsRead(notificationId);

      final notifications = state.notifications.map((n) {
        if (n.id == notificationId) {
          return ApplicationNotification(
            id: n.id,
            title: n.title,
            message: n.message,
            time: n.time,
            read: true,
          );
        }
        return n;
      }).toList();

      state = state.copyWith(notifications: notifications);
      print('✅ [NotificationProvider] Marked notification as read: $notificationId');
    } catch (e) {
      print('❌ [NotificationProvider] Mark as read error: $e');
      rethrow;
    }
  }

  /// Delete a notification
  Future<void> deleteNotification(String notificationId) async {
    try {
      await _notificationService.deleteNotification(notificationId);

      final notifications = state.notifications.where((n) => n.id != notificationId).toList();
      state = state.copyWith(notifications: notifications);
      print('✅ [NotificationProvider] Deleted notification: $notificationId');
    } catch (e) {
      print('❌ [NotificationProvider] Delete error: $e');
      rethrow;
    }
  }

  /// Clear all notifications (delete all notifications)
  Future<void> clearNotifications() async {
    try {
      final notifications = [...state.notifications];
      for (final notification in notifications) {
        await deleteNotification(notification.id);
      }
      state = state.copyWith(notifications: []);
      print('✅ [NotificationProvider] Cleared all notifications');
    } catch (e) {
      print('❌ [NotificationProvider] Clear all error: $e');
      rethrow;
    }
  }

  @override
  void dispose() {
    _webSocketService.removeListener(_handleWebSocketNotification);
    super.dispose();
  }
}

// Provider
final notificationProvider = StateNotifierProvider<NotificationNotifier, NotificationState>((ref) {
  return NotificationNotifier();
});