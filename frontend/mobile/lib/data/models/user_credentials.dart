class Permission {
  final String? id;
  final String? name;
  final String? slug;
  final String? description;

  Permission({
    this.id,
    this.name,
    this.slug,
    this.description,
  });

  factory Permission.fromJson(Map<String, dynamic> json) {
    print('🔎 Parsing Permission from JSON:');
    print(json);
    return Permission(
      id: json['id'] as String?,
      name: json['name'] as String?,
      slug: json['slug'] as String?,
      description: json['description'] as String?,
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
  final String? id;
  final String? name;
  final String? slug;
  final List<Permission>? permissions;

  Role({
    this.id,
    this.name,
    this.slug,
    this.permissions,
  });

  factory Role.fromJson(Map<String, dynamic> json) {
    print('🔎 Parsing Role from JSON:');
    print(json);
    return Role(
      id: json['id'] as String?,
      name: json['name'] as String?,
      slug: json['slug'] as String?,
      permissions: (json['permissions'] as List?)?.map((p) => Permission.fromJson(p)).toList() ?? [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'slug': slug,
      'permissions': permissions?.map((p) => p.toJson()).toList(),
    };
  }
}

class UserProfile {
  final String? id;
  final String? rsbsaId;
  final String? nationalId;
  final String? dateOfBirth;
  final String? gender;
  final String? civilStatus;
  final String? houseNo;
  final String? street;
  final String? barangay;
  final String? municipality;
  final String? province;
  final String? region;
  final String? farmerType;
  final String? primaryOccupation;
  final double? totalFarmAreaHa;
  final String? landTenure;
  final bool? pcicEnrolled;
  final String? pcicPolicyNumber;
  final String? pcicPolicyStart;
  final String? pcicPolicyEnd;
  final int? householdSize;
  final String? educationLevel;
  final double? annualFarmIncome;
  final String? createdAt;
  final String? updatedAt;

  UserProfile({
    this.id,
    this.rsbsaId,
    this.nationalId,
    this.dateOfBirth,
    this.gender,
    this.civilStatus,
    this.houseNo,
    this.street,
    this.barangay,
    this.municipality,
    this.province,
    this.region,
    this.farmerType,
    this.primaryOccupation,
    this.totalFarmAreaHa,
    this.landTenure,
    this.pcicEnrolled,
    this.pcicPolicyNumber,
    this.pcicPolicyStart,
    this.pcicPolicyEnd,
    this.householdSize,
    this.educationLevel,
    this.annualFarmIncome,
    this.createdAt,
    this.updatedAt,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    print('🔎 Parsing UserProfile from JSON:');
    print(json);
    return UserProfile(
      id: json['id'] as String?,
      rsbsaId: json['rsbsaId'] as String?,
      nationalId: json['nationalId'] as String?,
      dateOfBirth: json['dateOfBirth'] as String?,
      gender: json['gender'] as String?,
      civilStatus: json['civilStatus'] as String?,
      houseNo: json['houseNo'] as String?,
      street: json['street'] as String?,
      barangay: json['barangay'] as String?,
      municipality: json['municipality'] as String?,
      province: json['province'] as String?,
      region: json['region'] as String?,
      farmerType: json['farmerType'] as String?,
      primaryOccupation: json['primaryOccupation'] as String?,
      totalFarmAreaHa: (json['totalFarmAreaHa'] as num?)?.toDouble(),
      landTenure: json['landTenure'] as String?,
      pcicEnrolled: json['pcicEnrolled'] as bool? ?? false,
      pcicPolicyNumber: json['pcicPolicyNumber'] as String?,
      pcicPolicyStart: json['pcicPolicyStart'] as String?,
      pcicPolicyEnd: json['pcicPolicyEnd'] as String?,
      householdSize: json['householdSize'] as int?,
      educationLevel: json['educationLevel'] as String?,
      annualFarmIncome: (json['annualFarmIncome'] as num?)?.toDouble(),
      createdAt: json['createdAt'] as String?,
      updatedAt: json['updatedAt'] as String?,
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
  final String? username;
  final String? firstName;
  final String? lastName;
  final String? email;
  final String? phoneNumber;
  final List<Role>? roles;
  final UserProfile? profile;

  User({
    this.username,
    this.firstName,
    this.lastName,
    this.email,
    this.phoneNumber,
    this.roles,
    this.profile,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    print('🔎 Parsing User from JSON:');
    print(json);
    return User(
      username: json['username'] as String?,
      firstName: json['firstName'] as String?,
      lastName: json['lastName'] as String?,
      email: json['email'] as String?,
      phoneNumber: json['phoneNumber'] as String?,
      roles: (json['roles'] as List?)?.map((r) => Role.fromJson(r)).toList() ?? [],
      profile: json['profile'] != null ? UserProfile.fromJson(json['profile']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'firstName': firstName,
      'lastName': lastName,
      'email': email,
      'phoneNumber': phoneNumber,
      'roles': roles?.map((r) => r.toJson()).toList(),
      'profile': profile?.toJson(),
    };
  }
}

class UserCredentials {
  final String? id;
  final String? accessToken;
  final String? refreshToken;
  final String? websocketToken;
  final User? user;

  UserCredentials({
    this.id,
    this.accessToken,
    this.refreshToken,
    this.websocketToken,
    this.user,
  });

  factory UserCredentials.fromJson(Map<String, dynamic> json) {
    print('🔎 Parsing UserCredentials from JSON:');
    print(json);
    return UserCredentials(
      id: json['id'] as String?,
      accessToken: json['accessToken'] as String?,
      refreshToken: json['refreshToken'] as String?,
      websocketToken: json['websocketToken'] as String?,
      user: json['user'] != null ? User.fromJson(json['user']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'accessToken': accessToken,
      'refreshToken': refreshToken,
      'websocketToken': websocketToken,
      'user': user?.toJson(),
    };
  }
}
