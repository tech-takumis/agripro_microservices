import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:another_flushbar/flushbar.dart';
import 'package:mobile/features/messages/providers/notification_provider.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';
import 'package:mobile/data/models/api_error_response.dart';
import 'package:timeago/timeago.dart' as timeago;

class NotificationPage extends StatefulHookConsumerWidget {
  const NotificationPage({super.key});

  @override
  ConsumerState<NotificationPage> createState() => _NotificationPageState();
}

class _NotificationPageState extends ConsumerState<NotificationPage> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final authState = ref.read(authProvider);
      if (authState.userId != null) {
        ref.read(notificationProvider.notifier).fetchNotifications(authState.userId!);
      }
    });
  }

  void _showErrorFlushbar(BuildContext context, dynamic error) {
    String message = 'An error occurred';

    if (error is ApiErrorResponse) {
      message = error.message;
    } else if (error is Exception) {
      message = error.toString().replaceFirst('Exception: ', '');
    } else {
      message = error.toString();
    }

    Flushbar(
      message: message,
      backgroundColor: Colors.red.withOpacity(0.9),
      duration: const Duration(seconds: 4),
      margin: const EdgeInsets.all(12),
      borderRadius: BorderRadius.circular(8),
      flushbarPosition: FlushbarPosition.TOP,
      icon: const Icon(
        Icons.error,
        color: Colors.white,
      ),
    ).show(context);
  }

  Future<void> _deleteNotification(
    BuildContext context,
    String notificationId,
    dynamic notificationNotifier,
  ) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Notification'),
        content: const Text('Are you sure you want to delete this notification?'),
        actions: [
          TextButton(
            child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
            onPressed: () => Navigator.of(context).pop(false),
          ),
          TextButton(
            child: const Text('Delete', style: TextStyle(color: Colors.red)),
            onPressed: () => Navigator.of(context).pop(true),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      try {
        await notificationNotifier.deleteNotification(notificationId);
        if (context.mounted) {
          Flushbar(
            message: 'Notification deleted successfully',
            backgroundColor: Colors.green.withOpacity(0.9),
            duration: const Duration(seconds: 2),
            margin: const EdgeInsets.all(12),
            borderRadius: BorderRadius.circular(8),
            flushbarPosition: FlushbarPosition.TOP,
            icon: const Icon(
              Icons.check_circle,
              color: Colors.white,
            ),
          ).show(context);
        }
      } catch (e) {
        if (context.mounted) {
          _showErrorFlushbar(context, e);
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final notificationState = ref.watch(notificationProvider);
    final authState = ref.watch(authProvider);
    final notificationNotifier = ref.read(notificationProvider.notifier);

    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: const Text(
          'Notifications',
          style: TextStyle(
            color: Colors.black,
            fontWeight: FontWeight.bold,
            fontSize: 24,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        centerTitle: false,
        actions: [
          if (notificationState.notifications.isNotEmpty)
            IconButton(
              icon: const Icon(Icons.delete_sweep, color: Color.fromARGB(255, 234, 21, 21)),
              tooltip: 'Clear all notifications',
              onPressed: () async {
                final confirmed = await showDialog<bool>(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: const Text('Clear All Notifications'),
                    content: const Text('Are you sure you want to delete all notifications?'),
                    actions: [
                      TextButton(
                        child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
                        onPressed: () => Navigator.of(context).pop(false),
                      ),
                      TextButton(
                        child: const Text('Clear All', style: TextStyle(color: Colors.red)),
                        onPressed: () => Navigator.of(context).pop(true),
                      ),
                    ],
                  ),
                );
                if (confirmed == true) {
                  try {
                    await notificationNotifier.clearNotifications();
                    if (context.mounted) {
                      Flushbar(
                        message: 'All notifications cleared',
                        backgroundColor: Colors.green.withOpacity(0.9),
                        duration: const Duration(seconds: 2),
                        margin: const EdgeInsets.all(12),
                        borderRadius: BorderRadius.circular(8),
                        flushbarPosition: FlushbarPosition.TOP,
                        icon: const Icon(
                          Icons.check_circle,
                          color: Colors.white,
                        ),
                      ).show(context);
                    }
                  } catch (e) {
                    if (context.mounted) {
                      _showErrorFlushbar(context, e);
                    }
                  }
                }
              },
            ),
        ],
      ),

      body: RefreshIndicator(
        color: const Color(0xFF2E7D32),
        onRefresh: () async {
          if (authState.userId != null) {
            await notificationNotifier.fetchNotifications(authState.userId!);
          }
        },
        child: notificationState.isLoading && notificationState.notifications.isEmpty
            ? const Center(child: CircularProgressIndicator(color: Color(0xFF2E7D32)))
            : notificationState.notifications.isEmpty
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.notifications_none, size: 72, color: Colors.grey[400]),
                        const SizedBox(height: 12),
                        const Text(
                          'No notifications yet',
                          style: TextStyle(color: Colors.grey, fontSize: 16),
                        ),
                      ],
                    ),
                  )
                : ListView.separated(
                    physics: const AlwaysScrollableScrollPhysics(),
                    itemCount: notificationState.notifications.length,
                    separatorBuilder: (context, index) => Divider(
                      height: 1,
                      thickness: 0.5,
                      color: Colors.grey.shade300,
                      indent: 16,
                      endIndent: 16,
                    ),
                    itemBuilder: (context, index) {
                      final notification = notificationState.notifications[index];
                      return ListTile(
                        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        leading: CircleAvatar(
                          radius: 22,
                          backgroundColor: notification.read
                              ? Colors.grey.shade200
                              : const Color(0xFF2E7D32),
                          child: const Icon(Icons.notifications, color: Colors.white),
                        ),
                        title: Text(
                          notification.title,
                          style: TextStyle(
                            fontSize: 15,
                            fontWeight: notification.read ? FontWeight.bold : FontWeight.bold,
                            color: const Color.fromARGB(221, 0, 0, 0),
                          ),
                        ),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const SizedBox(height: 4),
                            Text(
                              notification.message,
                              style: const TextStyle(fontSize: 14, color: Color.fromARGB(221, 48, 46, 46)),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              timeago.format(notification.time),
                              style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                            ),
                          ],
                        ),
                        onTap: () async {
                          if (!notification.read) {
                            try {
                              await notificationNotifier.markAsRead(notification.id);
                            } catch (e) {
                              if (context.mounted) {
                                _showErrorFlushbar(context, e);
                              }
                            }
                          }
                        },
                        trailing: PopupMenuButton<String>(
                          onSelected: (value) async {
                            if (value == 'delete') {
                              await _deleteNotification(context, notification.id, notificationNotifier);
                            } else if (value == 'mark_read') {
                              try {
                                await notificationNotifier.markAsRead(notification.id);
                              } catch (e) {
                                if (context.mounted) {
                                  _showErrorFlushbar(context, e);
                                }
                              }
                            }
                          },
                          itemBuilder: (BuildContext context) => [
                            if (!notification.read)
                              const PopupMenuItem<String>(
                                value: 'mark_read',
                                child: Row(
                                  children: [
                                    Icon(Icons.mark_email_read, size: 18),
                                    SizedBox(width: 8),
                                    Text('Mark as read'),
                                  ],
                                ),
                              ),
                            const PopupMenuItem<String>(
                              value: 'delete',
                              child: Row(
                                children: [
                                  Icon(Icons.delete, size: 18, color: Colors.red),
                                  SizedBox(width: 8),
                                  Text('Delete', style: TextStyle(color: Colors.red)),
                                ],
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  ),
      ),
    );
  }
}
