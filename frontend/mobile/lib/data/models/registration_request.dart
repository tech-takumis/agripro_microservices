class RegistrationRequest {
  // User
  final String tenantKey;
  final String username;
  final String firstName;
  final String lastName;
  final String email;
  final String password;
  final List<String> roles;
  final UserProfile profile;

  RegistrationRequest({
    required this.tenantKey,
    required this.username,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.password,
    required this.roles,
    required this.profile,
  });

  Map<String, dynamic> toJson() {
    return {
      'tenantKey': "Farmer",
      'username': username,
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'password': password,
      'roles': "Farmer",
      'profile': profile.toJson(),
    };
  }
}

class UserProfile {
  // User Profile
  final String rsbsaId;
  final String? middleName;
  final String phoneNumber;
  final String gender;
  final String civilStatus;
  final String street;
  final String barangay;
  final String municipality;
  final String province;
  final String region;
  final String farmerType;
  final double totalFarmAreaHa;

  UserProfile({
    required this.rsbsaId,
    this.middleName,
    required this.phoneNumber,
    required this.gender,
    required this.civilStatus,
    required this.street,
    required this.barangay,
    required this.municipality,
    required this.province,
    required this.region,
    required this.farmerType,
    required this.totalFarmAreaHa,
  });

  Map<String, dynamic> toJson() {
    return {
      'rsbsaId': rsbsaId,
      'middleName': middleName,
      'gender': gender,
      'civilStatus': civilStatus,
      'street': street,
      'barangay': barangay,
      'municipality': municipality,
      'province': province,
      'region': region,
      'farmerType': farmerType,
      'totalFarmAreaHa': totalFarmAreaHa,
    };
  }

}
