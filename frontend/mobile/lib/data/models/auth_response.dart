import '../models/user_credentials.dart';

class AuthResponse {
  final bool success;
  final String? message;
  final UserCredentials? credentials;

  AuthResponse({
    required this.success,
    this.message,
    this.credentials,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      success: json['success'] ?? true,
      message: json['message'],
      credentials: json.containsKey('id') ? UserCredentials.fromJson(json) : null,
    );
  }
}
