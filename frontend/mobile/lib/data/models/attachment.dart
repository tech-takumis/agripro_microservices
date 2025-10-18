class Attachment {
  final String documentId; // UUID
  final String url;

  Attachment({
    required this.documentId,
    required this.url,
  });

  factory Attachment.fromJson(Map<String, dynamic> json) {
    return Attachment(
      documentId: json['documentId'],
      url: json['url'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'documentId': documentId,
      'url': url,
    };
  }

  Attachment copyWith({
    String? documentId,
    String? url,
  }) {
    return Attachment(
      documentId: documentId ?? this.documentId,
      url: url ?? this.url,
    );
  }
}
