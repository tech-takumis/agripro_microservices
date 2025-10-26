/// Utility to convert a farm location object to a single string address.
String farmLocationToString(Map<String, dynamic>? farmLocation) {
  if (farmLocation == null) return '';
  final barangay = farmLocation['barangay'] ?? '';
  final city = farmLocation['city'] ?? '';
  final province = farmLocation['province'] ?? '';
  final region = farmLocation['region'] ?? '';
  // Only include non-empty parts, join with comma
  return [barangay, city, province, region]
      .where((part) => part != null && part.toString().trim().isNotEmpty)
      .join(', ');
}

