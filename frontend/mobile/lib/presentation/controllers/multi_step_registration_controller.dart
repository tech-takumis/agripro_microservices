import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/registration_request.dart';
import 'package:mobile/data/models/registration_response.dart';
import 'package:mobile/data/services/auth_api_service.dart';
import 'package:mobile/injection_container.dart';

/// Controller for multi-step registration process
class MultiStepRegistrationController extends ChangeNotifier {
  // Current step tracking
  int _currentStep = 1;
  final int _totalSteps = 3;

  // Loading and message states
  bool _isLoading = false;
  String _errorMessage = '';
  String _successMessage = '';
  RegistrationResponse? _registrationResult;

  // Form keys for each step
  final step1FormKey = GlobalKey<FormState>();
  final step2FormKey = GlobalKey<FormState>();
  final step3FormKey = GlobalKey<FormState>();

  // Step 1: Basic Information Controllers
  final rsbsaNumber = TextEditingController();
  final firstNameController = TextEditingController();
  final lastNameController = TextEditingController();
  final middleNameController = TextEditingController();
  final emailController = TextEditingController();
  final phoneNumberController = TextEditingController();
  final barangayController = TextEditingController();

  // Step 1: Account Credentials
  final usernameController = TextEditingController();
  final passwordController = TextEditingController();

  // Gender and Civil Status
  String? selectedGender;
  String? selectedCivilStatus;
  final List<String> genderOptions = ['Male', 'Female', 'Other'];
  final List<String> civilStatusOptions = [
    'Single',
    'Married',
    'Widowed',
    'Separated',
    'Divorced'
  ];

  // Step 2: Geographic Information Controllers
  final zipCodeController = TextEditingController();
  // Geographic selections will be handled by the step widget

  // Step 3: Farm Information Controllers
  final farmLocationController = TextEditingController();
  final farmSizeController = TextEditingController();
  final primaryCropController = TextEditingController();
  // Dropdown selections will be handled by the step widget

  // Geographic data (to be set by step 2 widget)
  String selectedRegion = '';
  String selectedProvince = '';
  String selectedCity = '';
  String selectedTenureStatus = '';
  String selectedFarmType = '';

  // Getters
  int get currentStep => _currentStep;
  int get totalSteps => _totalSteps;
  bool get isLoading => _isLoading;
  String get errorMessage => _errorMessage;
  String get successMessage => _successMessage;
  RegistrationResponse? get registrationResult => _registrationResult;

  bool get canGoNext {
    switch (_currentStep) {
      case 1:
        return step1FormKey.currentState?.validate() ?? false;
      case 2:
        return step2FormKey.currentState?.validate() ?? false;
      case 3:
        return step3FormKey.currentState?.validate() ?? false;
      default:
        return false;
    }
  }

  bool get canGoPrevious => _currentStep > 1;
  bool get isLastStep => _currentStep == _totalSteps;

  /// Move to next step
  void nextStep(BuildContext context) {
    if (!canGoNext) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please fill in all required fields correctly'),
          backgroundColor: Colors.red[100],
        ),
      );
      return;
    }

    if (_currentStep < _totalSteps) {
      _currentStep++;
      clearMessages();
      notifyListeners();
    }
  }

  /// Move to previous step
  void previousStep() {
    if (_currentStep > 1) {
      _currentStep--;
      clearMessages();
      notifyListeners();
    }
  }

  /// Go to specific step
  void goToStep(int step) {
    if (step >= 1 && step <= _totalSteps) {
      _currentStep = step;
      clearMessages();
      notifyListeners();
    }
  }

  /// Update geographic selections from step 2 widget
  void updateGeographicData({
    required String region,
    required String province,
    required String city,
  }) {
    selectedRegion = region;
    selectedProvince = province;
    selectedCity = city;
    notifyListeners();
  }

  void updateFarmData({
    required String tenureStatus,
    required String farmType,
  }) {
    selectedTenureStatus = tenureStatus;
    selectedFarmType = farmType;
    notifyListeners();
  }


  Future<void> submitRegistration(BuildContext context) async {
    if (!step3FormKey.currentState!.validate()) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please fill in all required fields correctly'),
          backgroundColor: Colors.red[100],
        ),
      );
      return;
    }

    try {
      _isLoading = true;
      _errorMessage = '';
      _successMessage = '';
      _registrationResult = null;
      notifyListeners();

      // Build UserProfile object with all extra fields
      final userProfile = UserProfile(
        rsbsaId: rsbsaNumber.text.trim(),
        middleName: middleNameController.text.trim().isEmpty
            ? null
            : middleNameController.text.trim(),
        phoneNumber: phoneNumberController.text.trim(),
        gender: selectedGender ?? '',
        civilStatus: selectedCivilStatus ?? '',
        street: farmLocationController.text.trim(),
        barangay: barangayController.text.trim(),
        municipality: selectedCity,
        province: selectedProvince,
        region: selectedRegion,
        farmerType: selectedFarmType,
        totalFarmAreaHa: double.tryParse(farmSizeController.text.trim()) ?? 0.0,
      );

      // Build RegistrationRequest object as required by backend
      final request = RegistrationRequest(
        tenantKey: 'farmer',
        username: usernameController.text.trim(),
        firstName: firstNameController.text.trim(),
        lastName: lastNameController.text.trim(),
        email: emailController.text.trim(),
        password: passwordController.text.trim(),
        roles: ['farmer'],
        profile: userProfile,
      );

      final response = await getIt<AuthApiService>().register(request);
      _registrationResult = response;

      if (response.success) {
        _successMessage = response.displayMessage;
      } else {
        _errorMessage = response.displayMessage;
      }
      notifyListeners();
    } catch (e) {
      _errorMessage = 'An unexpected error occurred: ${e.toString()}';
      notifyListeners();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// Clear messages
  void clearMessages() {
    _errorMessage = '';
    _successMessage = '';
    _registrationResult = null;
    notifyListeners();
  }

  /// Reset form to start over
  void resetForm() {
    _currentStep = 1;
    clearMessages();

    // Clear all controllers
    rsbsaNumber.clear();
    firstNameController.clear();
    lastNameController.clear();
    middleNameController.clear();
    emailController.clear();
    phoneNumberController.clear();
    zipCodeController.clear();
    farmLocationController.clear();
    farmSizeController.clear();
    primaryCropController.clear();
    barangayController.clear();

    // Clear selections
    selectedRegion = '';
    selectedProvince = '';
    selectedCity = '';
    selectedTenureStatus = '';
    selectedFarmType = '';
    selectedGender = null;
    selectedCivilStatus = null;
    notifyListeners();
  }

  /// Validation helpers
  static bool isValidRsbsaId(String referenceNumber) {
    if (referenceNumber.isEmpty) return false;
    final cleanId = referenceNumber.replaceAll(RegExp(r'[\s-]'), '');
    final rsbsaPattern = RegExp(r'^[\d-]+$');
    return rsbsaPattern.hasMatch(referenceNumber) && cleanId.length >= 6;
  }

  static String formatRsbsaId(String input) {
    String cleaned = input.replaceAll(RegExp(r'[\s-]'), '');
    if (cleaned.length >= 6) {
      return '${cleaned.substring(0, 3)}-${cleaned.substring(3)}';
    }
    return input;
  }

  /// Get step titles for step indicator
  List<String> get stepTitles => [
    'Basic Info',
    'Location',
    'Farm Details',
  ];
}

final multiStepRegistrationProvider = ChangeNotifierProvider<MultiStepRegistrationController>((ref) {
  return MultiStepRegistrationController();
});
