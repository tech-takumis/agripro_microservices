
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
