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
