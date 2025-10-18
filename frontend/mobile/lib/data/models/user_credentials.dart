class Permission {
  final String id;
  final String name;
  final String slug;
  final String description;

  Permission({
    required this.id,
    required this.name,
    required this.slug,
    required this.description,
  });

  factory Permission.fromJson(Map<String, dynamic> json) {
    return Permission(
      id: json['id'],
      name: json['name'],
      slug: json['slug'],
      description: json['description'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'slug': slug,
      'description': description,
    };
  }
}

class Role {
  final String id;
  final String name;
  final String slug;
  final List<Permission> permissions;

  Role({
    required this.id,
    required this.name,
    required this.slug,
    required this.permissions,
  });

  factory Role.fromJson(Map<String, dynamic> json) {
    return Role(
      id: json['id'],
      name: json['name'],
      slug: json['slug'],
      permissions: (json['permissions'] as List)
          .map((p) => Permission.fromJson(p))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'slug': slug,
      'permissions': permissions.map((p) => p.toJson()).toList(),
    };
  }
}

class UserProfile {
  final String id;
  final String rsbsaId;
  final String? nationalId;
  final String dateOfBirth;
  final String gender;
  final String civilStatus;
  final String houseNo;
  final String street;
  final String barangay;
  final String municipality;
  final String province;
  final String region;
  final String farmerType;
  final String? primaryOccupation;
  final double totalFarmAreaHa;
  final String? landTenure;
  final bool pcicEnrolled;
  final String? pcicPolicyNumber;
  final String? pcicPolicyStart;
  final String? pcicPolicyEnd;
  final int? householdSize;
  final String? educationLevel;
  final double? annualFarmIncome;
  final String createdAt;
  final String updatedAt;

  UserProfile({
    required this.id,
    required this.rsbsaId,
    this.nationalId,
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
    this.primaryOccupation,
    required this.totalFarmAreaHa,
    this.landTenure,
    required this.pcicEnrolled,
    this.pcicPolicyNumber,
    this.pcicPolicyStart,
    this.pcicPolicyEnd,
    this.householdSize,
    this.educationLevel,
    this.annualFarmIncome,
    required this.createdAt,
    required this.updatedAt,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      id: json['id'],
      rsbsaId: json['rsbsaId'],
      nationalId: json['nationalId'],
      dateOfBirth: json['dateOfBirth'],
      gender: json['gender'],
      civilStatus: json['civilStatus'],
      houseNo: json['houseNo'],
      street: json['street'],
      barangay: json['barangay'],
      municipality: json['municipality'],
      province: json['province'],
      region: json['region'],
      farmerType: json['farmerType'],
      primaryOccupation: json['primaryOccupation'],
      totalFarmAreaHa: json['totalFarmAreaHa']?.toDouble() ?? 0.0,
      landTenure: json['landTenure'],
      pcicEnrolled: json['pcicEnrolled'] ?? false,
      pcicPolicyNumber: json['pcicPolicyNumber'],
      pcicPolicyStart: json['pcicPolicyStart'],
      pcicPolicyEnd: json['pcicPolicyEnd'],
      householdSize: json['householdSize'],
      educationLevel: json['educationLevel'],
      annualFarmIncome: json['annualFarmIncome']?.toDouble(),
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'rsbsaId': rsbsaId,
      'nationalId': nationalId,
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
      'primaryOccupation': primaryOccupation,
      'totalFarmAreaHa': totalFarmAreaHa,
      'landTenure': landTenure,
      'pcicEnrolled': pcicEnrolled,
      'pcicPolicyNumber': pcicPolicyNumber,
      'pcicPolicyStart': pcicPolicyStart,
      'pcicPolicyEnd': pcicPolicyEnd,
      'householdSize': householdSize,
      'educationLevel': educationLevel,
      'annualFarmIncome': annualFarmIncome,
      'createdAt': createdAt,
      'updatedAt': updatedAt,
    };
  }
}

class User {
  final String username;
  final String firstName;
  final String lastName;
  final String email;
  final String phoneNumber;
  final List<Role> roles;
  final UserProfile profile;

  User({
    required this.username,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.phoneNumber,
    required this.roles,
    required this.profile,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      username: json['username'],
      firstName: json['firstName'],
      lastName: json['lastName'],
      email: json['email'],
      phoneNumber: json['phoneNumber'],
      roles: (json['roles'] as List)
          .map((r) => Role.fromJson(r))
          .toList(),
      profile: UserProfile.fromJson(json['profile']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'phoneNumber': phoneNumber,
      'roles': roles.map((r) => r.toJson()).toList(),
      'profile': profile.toJson(),
    };
  }
}

class UserCredentials {
  final String id;
  final String accessToken;
  final String refreshToken;
  final String websocketToken;
  final User user;

  UserCredentials({
    required this.id,
    required this.accessToken,
    required this.refreshToken,
    required this.websocketToken,
    required this.user,
  });

  factory UserCredentials.fromJson(Map<String, dynamic> json) {
    return UserCredentials(
      id: json['id'],
      accessToken: json['accessToken'],
      refreshToken: json['refreshToken'],
      websocketToken: json['websocketToken'],
      user: User.fromJson(json['user']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'accessToken': accessToken,
      'refreshToken': refreshToken,
      'websocketToken': websocketToken,
      'user': user.toJson(),
    };
  }
}
