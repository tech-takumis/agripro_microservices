class RegistrationRequest {
  // Basic information
  final String rsbsaNumber;
  final String firstName;
  final String lastName;
  final String? middleName;
  final String email;
  final String phoneNumber;

  // Demographics
  final String city;
  final String state;
  final String country;
  final String zipCode;

  // Farmer farm location
  final String farmLocation;
  final String tenureStatus;
  final String farmSize;
  final String farmType;
  final String primaryCrop;

  RegistrationRequest({
    required this.rsbsaNumber,
    required this.firstName,
    required this.lastName,
    this.middleName,
    required this.email,
    required this.phoneNumber,
    required this.city,
    required this.state,
    required this.country,
    required this.zipCode,
    required this.farmLocation,
    required this.tenureStatus,
    required this.farmSize,
    required this.farmType,
    required this.primaryCrop,
  });

  Map<String, dynamic> toJson() {
    return {
      'rsbsaNumber': rsbsaNumber,
      'firstName': firstName,
      'lastName': lastName,
      'middleName': middleName,
      'email': email,
      'phoneNumber': phoneNumber,
      'city': city,
      'state': state,
      'country': country,
      'zipCode': zipCode,
      'farmLocation': farmLocation,
      'tenureStatus': tenureStatus,
      'farmSize': farmSize,
      'farmType': farmType,
      'primaryCrop': primaryCrop,
    };
  }
}
