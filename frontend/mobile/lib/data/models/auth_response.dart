class AuthResponse {
  final String? accessToken;
  final String? refreshToken;
  final String message;
  final bool success;

  AuthResponse({
    this.accessToken,
    this.refreshToken,
    required this.message,
    required this.success,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      accessToken: json['accessToken'],
      refreshToken: json['refreshToken'],
      message: json['message'] ?? '',
      success: json['accessToken'] != null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'accessToken': accessToken,
      'refreshToken': refreshToken,
      'message': message,
      'success': success,
    };
  }
}
