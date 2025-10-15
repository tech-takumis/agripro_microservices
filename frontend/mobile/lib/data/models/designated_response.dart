class DesignatedResponse {
  final String userId;
  final String serviceType;

  DesignatedResponse({
    required this.userId,
    required this.serviceType,
  });

  factory DesignatedResponse.fromJson(Map<String, dynamic> json) {
    return DesignatedResponse(
      userId: json['userId'],
      serviceType: json['serviceType'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'serviceType': serviceType,
    };
  }
}
