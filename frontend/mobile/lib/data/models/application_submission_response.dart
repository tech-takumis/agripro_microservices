class ApplicationSubmissionResponse {
  final bool success;
  final String message;
  final String? error;
  final dynamic errors;

  ApplicationSubmissionResponse({
    required this.success,
    required this.message,
    this.error,
    this.errors,
  });

  factory ApplicationSubmissionResponse.fromJson(Map<String, dynamic> json) {
    return ApplicationSubmissionResponse(
      success: json['success'] ?? false,
      message: json['message'] ?? '',
      error: json['error'],
      errors: json['errors'],
    );
  }
}
class ApplicationSubmissionRequest {
  final String applicationTypeId;
  final Map<String, dynamic> fieldValues;
  final List<String>? documentIds;

  ApplicationSubmissionRequest({
    required this.applicationTypeId,
    required this.fieldValues,
    this.documentIds,
  });

  Map<String, dynamic> toJson() => {
    'applicationTypeId': applicationTypeId,
    'fieldValues': fieldValues,
    if (documentIds != null) 'documentIds': documentIds,
  };
}

