import 'package:mobile/data/models/registration_request.dart';
import 'package:mobile/data/models/registration_response.dart';
import 'package:mobile/data/services/auth_api_service.dart';
import 'package:mobile/injection_container.dart';

class RegistrationController {
  bool _isLoading = false;
  String _errorMessage = '';
  String _successMessage = '';
  RegistrationResponse? _registrationResult;

  bool get isLoading => _isLoading;
  String get errorMessage => _errorMessage;
  String get successMessage => _successMessage;
  RegistrationResponse? get registrationResult => _registrationResult;

  Future<void> register(
    String rsbsaId,
    String firstName,
    String lastName,
    String password,
    String? middleName,
    String email,
    String phoneNumber,
    String dateOfBirth, // format: dd-MM-yyyy
    String gender,
    String civilStatus,
    String houseNo,
    String street,
    String barangay,
    String municipality,
    String province,
    String region,
    String farmerType,
    double totalFarmAreaHa,
  ) async {
    try {
      _isLoading = true;
      _errorMessage = '';
      _successMessage = '';
      _registrationResult = null;

      final request = RegistrationRequest(
        rsbsaId: rsbsaId,
        firstName: firstName,
        lastName: lastName,
        password: password,
        middleName: middleName,
        email: email,
        phoneNumber: phoneNumber,
        dateOfBirth: dateOfBirth,
        gender: gender,
        civilStatus: civilStatus,
        houseNo: houseNo,
        street: street,
        barangay: barangay,
        municipality: municipality,
        province: province,
        region: region,
        farmerType: farmerType,
        totalFarmAreaHa: totalFarmAreaHa,
      );

      final response = await getIt<AuthApiService>().register(request);

      _registrationResult = response;

      if (response.success) {
        _successMessage = response.displayMessage;
      } else {
        _errorMessage = response.displayMessage;
      }
    } catch (e) {
      _errorMessage = 'An unexpected error occurred: ${e.toString()}';
    } finally {
      _isLoading = false;
    }
  }

  void clearMessages() {
    _errorMessage = '';
    _successMessage = '';
    _registrationResult = null;
  }

  static bool isValidRsbsaId(String rsbsaNumber) {
    if (rsbsaNumber.isEmpty) return false;
    final cleanId = rsbsaNumber.replaceAll(RegExp(r'[\s-]'), '');
    final rsbsaPattern = RegExp(r'^[\d-]+$');
    return rsbsaPattern.hasMatch(rsbsaNumber) && cleanId.length >= 6;
  }

  static String formatRsbsaId(String input) {
    String cleaned = input.replaceAll(RegExp(r'[\s-]'), '');
    if (cleaned.length >= 6) {
      return '${cleaned.substring(0, 3)}-${cleaned.substring(3)}';
    }
    return input;
  }
}
