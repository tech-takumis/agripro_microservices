class DocumentResponse {
  final String documentId;
  final String referenceId;
  final String uploadedBy;
  final String fileName;
  final String fileType;
  final int? fileSize;
  final String objectKey;
  final Map<String, dynamic>? metaData;
  final String uploadedAt;
  final String preview;

  DocumentResponse({
    required this.documentId,
    required this.referenceId,
    required this.uploadedBy,
    required this.fileName,
    required this.fileType,
    this.fileSize,
    required this.objectKey,
    this.metaData,
    required this.uploadedAt,
    required this.preview,
  });

  factory DocumentResponse.fromJson(Map<String, dynamic> json) {
    return DocumentResponse(
      documentId: json['documentId']?.toString() ?? '',
      referenceId: json['referenceId']?.toString() ?? '',
      uploadedBy: json['uploadedBy']?.toString() ?? '',
      fileName: json['fileName'] ?? '',
      fileType: json['fileType'] ?? '',
      fileSize: json['fileSize'],
      objectKey: json['objectKey'] ?? '',
      metaData: json['metaData'] as Map<String, dynamic>?,
      uploadedAt: json['uploadedAt'] ?? '',
      preview: json['preview'] ?? '',
    );
  }
}
