import 'package:flutter/material.dart';

class ApiErrorResponse {
  final bool success;
  final String message;
  final int status;
  final DateTime timestamp;
  final Map<String, Object>? details;

  ApiErrorResponse({
    required this.success,
    required this.message,
    required this.status,
    required this.timestamp,
    this.details,
  });

  String get formattedMessage {
    if (details != null && details!.isNotEmpty) {
      final buffer = StringBuffer(message);
      buffer.write('\n');
      details!.forEach((key, value) {
        buffer.write('$key: $value\n');
      });
      return buffer.toString().trim();
    }
    return message;
  }

  Color get statusColor {
    if (status >= 500) {
      return Colors.red; // Server errors
    } else if (status >= 400) {
      return Colors.orange; // Client errors
    } else if (status >= 300) {
      return Colors.blue; // Redirects
    } else if (status >= 200) {
      return Colors.green; // Success
    }
    return Colors.grey; // Unknown
  }

  factory ApiErrorResponse.fromJson(Map<String, dynamic> json) {
    return ApiErrorResponse(
      success: json['success'] ?? false,
      message: json['message'] ?? '',
      status: json['status'] ?? 500,
      timestamp: DateTime.parse(json['timestamp'] ?? DateTime.now().toIso8601String()),
      details: json['details'] as Map<String, Object>?,
    );
  }
}
