import 'package:flutter/material.dart';

class ApiErrorResponse {
  final bool success;
  final String message;
  final int status;
  final DateTime timestamp;
  final Map<String, dynamic>? details;

  ApiErrorResponse({
    required this.success,
    required this.message,
    required this.status,
    required this.timestamp,
    this.details,
  });

  factory ApiErrorResponse.fromJson(Map<String, dynamic> json) {
    return ApiErrorResponse(
      success: json['success'] as bool? ?? false,
      message: json['message'] as String? ?? 'Unknown error occurred',
      status: json['status'] as int? ?? 500,
      timestamp: json['timestamp'] != null
          ? DateTime.parse(json['timestamp'] as String)
          : DateTime.now(),
      details: json['details'] as Map<String, dynamic>?,
    );
  }

  String get formattedMessage {
    if (details != null && details!['error'] != null) {
      return '${details!['error']}';
    }
    return message;
  }

  Color get statusColor {
    if (status >= 500) return Colors.red;
    if (status >= 400) return Colors.orange;
    return Colors.green;
  }
}
