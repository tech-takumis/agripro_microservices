
/// Model for application submission request
class ApplicationSubmissionDto {
  final String applicationTypeId;
  final Map<String, dynamic> fieldValues;

  ApplicationSubmissionDto({
    required this.applicationTypeId,
    required this.fieldValues,
  });

  Map<String, dynamic> toJson() {
    return {
      'applicationTypeId': applicationTypeId,
      'fieldValues': fieldValues,
    };
  }
}


/// Model for application submission response
class ApplicationSubmissionResponse {
  final bool success;
  final String message;
  final String? applicationId;
  final dynamic errors;

  ApplicationSubmissionResponse({
    required this.success,
    required this.message,
    this.applicationId,
    this.errors,
  });

  factory ApplicationSubmissionResponse.fromJson(Map<String, dynamic> json) {
    return ApplicationSubmissionResponse(
      success: json['success'] as bool,
      message: json['message'] as String,
      applicationId: json['applicationId'] as String?,
      errors: json['errors'],
    );
  }
}
