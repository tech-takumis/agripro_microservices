class RememberMe {
  final String username;
  final String password;

  RememberMe({
    required this.username,
    required this.password,
  });

  factory RememberMe.fromJson(Map<String, dynamic> json) {
    return RememberMe(
      username: json['username'],
      password: json['passord'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'firstName': password,
    };
  }
}