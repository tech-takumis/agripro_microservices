class RegistrationResponse {
  final String? message;
  final String? username;
  final String? error;
  final String? timestamp;
  final int? status;
  final bool success;

  RegistrationResponse({
    this.message,
    this.username,
    this.error,
    this.timestamp,
    this.status,
    required this.success,
  });

  factory RegistrationResponse.fromJson(Map<String, dynamic> json) {
    return RegistrationResponse(
      message: json['message'],
      username: json['username'],
      error: json['error'],
      timestamp: json['timestamp'],
      status: json['status'],
      success: json['username'] != null && json['status'] == 201,
    );
  }

  String get displayMessage {
    if (success && message != null) {
      return message!;
    } else if (error != null && message != null) {
      return message!;
    } else if (error != null) {
      return error!;
    } else {
      return 'Registration failed. Please try again.';
    }
  }
}
