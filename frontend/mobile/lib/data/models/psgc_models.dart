/// Models for Philippine Standard Geographic Code (PSGC) API
/// API Documentation: https://psgc.gitlab.io/api/

class PSGCRegion {
  final String code;
  final String name;
  final String regionName;

  PSGCRegion({
    required this.code,
    required this.name,
    required this.regionName,
  });

  factory PSGCRegion.fromJson(Map<String, dynamic> json) {
    return PSGCRegion(
      code: json['code'] ?? '',
      name: json['name'] ?? '',
      regionName: json['regionName'] ?? json['name'] ?? '',
    );
  }

  @override
  String toString() => regionName;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
          other is PSGCRegion && runtimeType == other.runtimeType && code == other.code;

  @override
  int get hashCode => code.hashCode;
}

class PSGCProvince {
  final String code;
  final String name;
  final String regionCode;

  PSGCProvince({
    required this.code,
    required this.name,
    required this.regionCode,
  });

  factory PSGCProvince.fromJson(Map<String, dynamic> json) {
    return PSGCProvince(
      code: json['code'] ?? '',
      name: json['name'] ?? '',
      regionCode: json['regionCode'] ?? '',
    );
  }

  @override
  String toString() => name;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
          other is PSGCProvince && runtimeType == other.runtimeType && code == other.code;

  @override
  int get hashCode => code.hashCode;
}

class PSGCCity {
  final String code;
  final String name;
  final String provinceCode;
  final bool isCity;

  PSGCCity({
    required this.code,
    required this.name,
    required this.provinceCode,
    this.isCity = false,
  });

  factory PSGCCity.fromJson(Map<String, dynamic> json) {
    return PSGCCity(
      code: json['code'] ?? '',
      name: json['name'] ?? '',
      provinceCode: json['provinceCode'] ?? '',
      isCity: json['isCity'] ?? false,
    );
  }

  @override
  String toString() => name;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
          other is PSGCCity && runtimeType == other.runtimeType && code == other.code;

  @override
  int get hashCode => code.hashCode;
}

class PSGCBarangay {
  final String code;
  final String name;
  final String cityCode;

  PSGCBarangay({
    required this.code,
    required this.name,
    required this.cityCode,
  });

  factory PSGCBarangay.fromJson(Map<String, dynamic> json) {
    return PSGCBarangay(
      code: json['code'] ?? '',
      name: json['name'] ?? '',
      // API may return municipalityCode instead of cityCode in this endpoint
      cityCode: json['cityCode'] ?? json['municipalityCode'] ?? '',
    );
  }

  @override
  String toString() => name;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
          other is PSGCBarangay && runtimeType == other.runtimeType && code == other.code;

  @override
  int get hashCode => code.hashCode;
}
