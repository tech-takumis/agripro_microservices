import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:mobile/data/models/psgc_models.dart';

/// Service for Philippine Standard Geographic Code (PSGC) API
/// Provides access to regions, provinces, cities, and barangays
class PSGCService {
  final Dio _dio;
  static const String baseUrl = 'https://psgc.gitlab.io/api';

  // Cache for frequently accessed data
  List<PSGCRegion>? _cachedRegions;
  final Map<String, List<PSGCProvince>> _cachedProvinces = {};
  final Map<String, List<PSGCCity>> _cachedCities = {};
  final Map<String, List<PSGCBarangay>> _cachedBarangays = {};

  PSGCService(this._dio) {
    _initializeDio();
  }

  void _initializeDio() {
    _dio.options = BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      responseType: ResponseType.json, // ensure JSON decoding by Dio
    );

    _dio.interceptors.add(
      LogInterceptor(
        requestBody: false,
        responseBody: false,
        requestHeader: false,
        responseHeader: false,
        error: true,
        logPrint: (obj) => print('🌍 PSGC API: $obj'),
      ),
    );
  }

  // Helper to normalize API payloads into a List<dynamic>
  List<dynamic> _ensureList(dynamic data) {
    if (data is List) return data;

    if (data is String) {
      final decoded = jsonDecode(data);
      if (decoded is List) return decoded;
      if (decoded is Map && decoded['data'] is List) return decoded['data'] as List<dynamic>;
    }

    if (data is Map && data['data'] is List) {
      return data['data'] as List<dynamic>;
    }

    throw Exception('Unexpected response format from PSGC API.');
  }

  /// Fetch all regions in the Philippines
  Future<List<PSGCRegion>> getRegions() async {
    if (_cachedRegions != null) {
      return _cachedRegions!;
    }

    try {
      print('🚀 Fetching regions from PSGC API');
      final response = await _dio.get('/regions');

      final List<dynamic> data = _ensureList(response.data);
      _cachedRegions = data.map((json) => PSGCRegion.fromJson(json as Map<String, dynamic>)).toList();

      print('✅ Fetched ${_cachedRegions!.length} regions');
      return _cachedRegions!;
    } on DioException catch (e) {
      print('❌ Failed to fetch regions: ${e.message}');
      throw Exception('Failed to load regions. Please check your internet connection.');
    } on FormatException catch (e) {
      print('❌ Failed to parse regions: ${e.message}');
      throw Exception('Failed to load regions. Unexpected response format.');
    }
  }

  /// Fetch provinces by region code
  Future<List<PSGCProvince>> getProvincesByRegion(String regionCode) async {
    if (_cachedProvinces.containsKey(regionCode)) {
      return _cachedProvinces[regionCode]!;
    }

    try {
      print('🚀 Fetching provinces for region: $regionCode');
      final response = await _dio.get('/regions/$regionCode/provinces');

      final List<dynamic> data = _ensureList(response.data);
      final provinces = data.map((json) => PSGCProvince.fromJson(json as Map<String, dynamic>)).toList();

      _cachedProvinces[regionCode] = provinces;
      print('✅ Fetched ${provinces.length} provinces for region $regionCode');
      return provinces;
    } on DioException catch (e) {
      print('❌ Failed to fetch provinces: ${e.message}');
      throw Exception('Failed to load provinces. Please check your internet connection.');
    } on FormatException catch (e) {
      print('❌ Failed to parse provinces: ${e.message}');
      throw Exception('Failed to load provinces. Unexpected response format.');
    }
  }

  /// Fetch cities/municipalities by province code
  Future<List<PSGCCity>> getCitiesByProvince(String provinceCode) async {
    if (_cachedCities.containsKey(provinceCode)) {
      return _cachedCities[provinceCode]!;
    }

    try {
      print('🚀 Fetching cities for province: $provinceCode');
      final response = await _dio.get('/provinces/$provinceCode/cities-municipalities');

      final List<dynamic> data = _ensureList(response.data);
      final cities = data.map((json) => PSGCCity.fromJson(json as Map<String, dynamic>)).toList();

      _cachedCities[provinceCode] = cities;
      print('✅ Fetched ${cities.length} cities for province $provinceCode');
      return cities;
    } on DioException catch (e) {
      print('❌ Failed to fetch cities: ${e.message}');
      throw Exception('Failed to load cities. Please check your internet connection.');
    } on FormatException catch (e) {
      print('❌ Failed to parse cities: ${e.message}');
      throw Exception('Failed to load cities. Unexpected response format.');
    }
  }

  /// Fetch barangays by city/municipality code
  Future<List<PSGCBarangay>> getBarangaysByCity(String cityCode) async {
    if (_cachedBarangays.containsKey(cityCode)) {
      print('🗂 Returning cached barangays for city: $cityCode');
      return _cachedBarangays[cityCode]!;
    }

    try {
      print('🚀 Fetching barangays for city: $cityCode');
      final response = await _dio.get('/cities-municipalities/$cityCode/barangays');
      print('🌍 PSGC API: Response status: [32m${response.statusCode}[0m');
      print('🌍 PSGC API: Response data: ${response.data}');

      final List<dynamic> data = _ensureList(response.data);
      print('🌍 PSGC API: Parsed barangays count: ${data.length}');
      final barangays = data.map((json) {
        try {
          return PSGCBarangay.fromJson(json as Map<String, dynamic>);
        } catch (e) {
          print('❌ Error parsing barangay: $json, error: $e');
          rethrow;
        }
      }).toList();

      _cachedBarangays[cityCode] = barangays;
      print('✅ Fetched ${barangays.length} barangays for city $cityCode');
      return barangays;
    } on DioException catch (e) {
      print('❌ DioException: ${e.message}');
      print('❌ DioException response: ${e.response?.data}');
      throw Exception('Failed to load barangays. Please check your internet connection or API response.');
    } on FormatException catch (e) {
      print('❌ FormatException: ${e.message}');
      throw Exception('Failed to load barangays. Unexpected response format.');
    } catch (e) {
      print('❌ Unknown error: $e');
      throw Exception('Failed to load barangays. Unknown error occurred.');
    }
  }

  /// Clear all cached data
  void clearCache() {
    _cachedRegions = null;
    _cachedProvinces.clear();
    _cachedCities.clear();
    _cachedBarangays.clear();
    print('🧹 PSGC cache cleared');
  }

  /// Search cities across all provinces (useful for autocomplete)
  Future<List<PSGCCity>> searchCities(String query) async {
    if (query.length < 2) return [];

    try {
      final response = await _dio.get('/cities-municipalities');
      final List<dynamic> data = _ensureList(response.data);

      final allCities = data.map((json) => PSGCCity.fromJson(json as Map<String, dynamic>)).toList();

      return allCities
          .where((city) => city.name.toLowerCase().contains(query.toLowerCase()))
          .take(20) // Limit results
          .toList();
    } on DioException catch (e) {
      print('❌ Failed to search cities: ${e.message}');
      return [];
    } on FormatException catch (e) {
      print('❌ Failed to parse search results: ${e.message}');
      return [];
    }
  }
}
