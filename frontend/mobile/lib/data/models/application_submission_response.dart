class ApplicationSubmissionResponse {
  final bool success;
  final String message;
  final String? error;

  ApplicationSubmissionResponse({
    this.success = true,
    required this.message,
    this.error,
  });

  factory ApplicationSubmissionResponse.fromJson(Map<String, dynamic> json) {
    return ApplicationSubmissionResponse(
      success: json['success'] ?? true,
      message: json['message'] ?? 'Success',
      error: json['error'],
    );
  }
}
