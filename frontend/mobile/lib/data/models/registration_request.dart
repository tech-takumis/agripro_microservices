class RegistrationRequest {
  final String referenceNumber;

  RegistrationRequest({required this.referenceNumber});

  Map<String, dynamic> toJson() {
    return {'referenceNumber': referenceNumber};
  }
}
