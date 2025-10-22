class RegistrationRequest {
  // User
  final String rsbsaId;
  final String username;
  final String password;
  final String firstName;
  final String lastName;
  final String? middleName;
  final String email;
  final String phoneNumber;
  final UserProfile userProfile;

  RegistrationRequest({
    required this.rsbsaId,
    required this.username,
    required this.password,
    required this.firstName,
    required this.lastName,
    this.middleName,
    required this.email,
    required this.phoneNumber,
    required this.userProfile
  });

  Map<String, dynamic> toJson() {
    return {
      'rsbsaId': rsbsaId,
      'username': username,
      'password': password,
      'firstName': firstName,
      'lastName': lastName,
      'middleName': middleName,
      'email': email,
      'phoneNumber': phoneNumber,
      'userProfile': userProfile.toJson(),
    };
  }
}

class UserProfile {
  // User Profile
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
