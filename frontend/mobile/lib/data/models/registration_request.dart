class RegistrationRequest {
  // User
  final String rsbsaId;
  final String firstName;
  final String lastName;
  final String password;
  final String? middleName;
  final String email;
  final String phoneNumber;

  // User Profile
  final String dateOfBirth; // format: dd-MM-yyyy
  final String gender;
  final String civilStatus;

  final String houseNo;
  final String street;
  final String barangay;
  final String municipality;
  final String province;
  final String region;

  final String farmerType;
  final double totalFarmAreaHa;

  RegistrationRequest({
    required this.rsbsaId,
    required this.firstName,
    required this.lastName,
    required this.password,
    this.middleName,
    required this.email,
    required this.phoneNumber,
    required this.dateOfBirth,
    required this.gender,
    required this.civilStatus,
    required this.houseNo,
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
      'firstName': firstName,
      'lastName': lastName,
      'password': password,
      'middleName': middleName,
      'email': email,
      'phoneNumber': phoneNumber,
      'dateOfBirth': dateOfBirth,
      'gender': gender,
      'civilStatus': civilStatus,
      'houseNo': houseNo,
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
