import 'package:flutter/material.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:mobile/features/messages/providers/notification_provider.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';
import 'package:mobile/presentation/widgets/error_display.dart';
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
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('All notifications cleared')),
                      );
                    }
                  } catch (e) {
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Failed to clear notifications: $e')),
                      );
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
                                ErrorDisplay.showError(context, e);
                              }
                            }
                          }
                        },
                      );
                    },
                  ),
      ),
    );
  }
}
