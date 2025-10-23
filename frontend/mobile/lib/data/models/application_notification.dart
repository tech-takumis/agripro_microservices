import 'package:uuid/uuid.dart';

class ApplicationNotification {
  final String id;
  final String title;
  final String message;
  final DateTime time;
  final bool read;

  ApplicationNotification({
    required this.id,
    required this.title,
    required this.message,
    required this.time,
    required this.read,
  });

  factory ApplicationNotification.fromJson(Map<String, dynamic> json) {
    return ApplicationNotification(
      id: json['id'] ?? const Uuid().v4(),
      title: json['title'] ?? '',
      message: json['message'] ?? '',
      time: json['time'] != null ? DateTime.parse(json['time']) : DateTime.now(),
      read: json['read'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'message': message,
      'time': time.toIso8601String(),
      'read': read,
    };
  }
}

