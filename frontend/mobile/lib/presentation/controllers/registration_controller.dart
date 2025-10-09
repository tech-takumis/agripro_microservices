import 'package:get/get.dart';
import 'package:mobile/data/models/registration_request.dart';
import 'package:mobile/data/models/registration_response.dart';
import 'package:mobile/data/services/api_service.dart';

class RegistrationController extends GetxController {
  final _isLoading = false.obs;
  final _errorMessage = ''.obs;
  final _successMessage = ''.obs;
  final _registrationResult = Rxn<RegistrationResponse>();

  bool get isLoading => _isLoading.value;
  String get errorMessage => _errorMessage.value;
  String get successMessage => _successMessage.value;
  RegistrationResponse? get registrationResult => _registrationResult.value;

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
      _isLoading.value = true;
      _errorMessage.value = '';
      _successMessage.value = '';
      _registrationResult.value = null;

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

      final response = await ApiService.to.register(request);

      _registrationResult.value = response;

      if (response.success) {
        _successMessage.value = response.displayMessage;
      } else {
        _errorMessage.value = response.displayMessage;
      }
    } catch (e) {
      _errorMessage.value = 'An unexpected error occurred: ${e.toString()}';
    } finally {
      _isLoading.value = false;
    }
  }

  void clearMessages() {
    _errorMessage.value = '';
    _successMessage.value = '';
    _registrationResult.value = null;
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
