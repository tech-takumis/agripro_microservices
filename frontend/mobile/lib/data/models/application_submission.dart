class ApplicationSubmissionRequest {
  final String applicationTypeId;
  final Map<String, dynamic> fieldValues;
  final List<String> documentIds;

  ApplicationSubmissionRequest({
    required this.applicationTypeId,
    required this.fieldValues,
    required this.documentIds,
  });

  Map<String, dynamic> toJson() {
    return {
      'applicationTypeId': applicationTypeId,
      'fieldValues': fieldValues,
      'documentIds': documentIds,
    };
  }
}


class ApplicationSubmissionResponse {
  final bool success;
  final String message;
  final String applicationId;

  ApplicationSubmissionResponse({
    required this.success,
    required this.message,
    required this.applicationId,
  });

  factory ApplicationSubmissionResponse.fromJson(Map<String, dynamic> json) {
    return ApplicationSubmissionResponse(
      success: json['success'] ?? false,
      message: json['message'] ?? '',
      applicationId: json['applicationId']?.toString() ?? '',
    );
  }
}
