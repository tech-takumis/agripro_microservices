import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile/data/models/registration_request.dart';
import 'package:mobile/data/models/registration_response.dart';
import 'package:mobile/data/services/api_service.dart';
import 'package:mobile/data/services/psgc_service.dart';

/// Controller for multi-step registration process
class MultiStepRegistrationController extends GetxController {
  // Current step tracking
  final _currentStep = 1.obs;
  final _totalSteps = 3;

  // Loading and message states
  final _isLoading = false.obs;
  final _errorMessage = ''.obs;
  final _successMessage = ''.obs;
  final _registrationResult = Rxn<RegistrationResponse>();

  // Form keys for each step
  final step1FormKey = GlobalKey<FormState>();
  final step2FormKey = GlobalKey<FormState>();
  final step3FormKey = GlobalKey<FormState>();

  // Step 1: Basic Information Controllers
  final referenceNumberController = TextEditingController();
  final firstNameController = TextEditingController();
  final lastNameController = TextEditingController();
  final middleNameController = TextEditingController();
  final emailController = TextEditingController();
  final phoneNumberController = TextEditingController();

  // Step 2: Geographic Information Controllers
  final zipCodeController = TextEditingController();
  // Geographic selections will be handled by the step widget

  // Step 3: Farm Information Controllers
  final farmAddressController = TextEditingController();
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
  int get currentStep => _currentStep.value;
  int get totalSteps => _totalSteps;
  bool get isLoading => _isLoading.value;
  String get errorMessage => _errorMessage.value;
  String get successMessage => _successMessage.value;
  RegistrationResponse? get registrationResult => _registrationResult.value;

  bool get canGoNext {
    switch (_currentStep.value) {
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

  bool get canGoPrevious => _currentStep.value > 1;
  bool get isLastStep => _currentStep.value == _totalSteps;

  @override
  void onInit() {
    super.onInit();
    // Initialize PSGC service if not already initialized
    if (!Get.isRegistered<PSGCService>()) {
      Get.put(PSGCService());
    }
  }

  @override
  void onClose() {
    // Dispose controllers
    referenceNumberController.dispose();
    firstNameController.dispose();
    lastNameController.dispose();
    middleNameController.dispose();
    emailController.dispose();
    phoneNumberController.dispose();
    zipCodeController.dispose();
    farmAddressController.dispose();
    farmSizeController.dispose();
    primaryCropController.dispose();
    super.onClose();
  }

  /// Move to next step
  void nextStep() {
    if (!canGoNext) {
      Get.snackbar(
        'Validation Error',
        'Please fill in all required fields correctly',
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: Colors.red[100],
        colorText: Colors.red[800],
      );
      return;
    }

    if (_currentStep.value < _totalSteps) {
      _currentStep.value++;
      clearMessages();
    }
  }

  /// Move to previous step
  void previousStep() {
    if (_currentStep.value > 1) {
      _currentStep.value--;
      clearMessages();
    }
  }

  /// Go to specific step
  void goToStep(int step) {
    if (step >= 1 && step <= _totalSteps) {
      _currentStep.value = step;
      clearMessages();
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
  }

  /// Update farm data from step 3 widget
  void updateFarmData({
    required String tenureStatus,
    required String farmType,
  }) {
    selectedTenureStatus = tenureStatus;
    selectedFarmType = farmType;
  }

  /// Submit registration
  Future<void> submitRegistration() async {
    if (!step3FormKey.currentState!.validate()) {
      Get.snackbar(
        'Validation Error',
        'Please fill in all required fields correctly',
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: Colors.red[100],
        colorText: Colors.red[800],
      );
      return;
    }

    try {
      _isLoading.value = true;
      _errorMessage.value = '';
      _successMessage.value = '';
      _registrationResult.value = null;

      final request = RegistrationRequest(
        // Basic Information
        referenceNumber: referenceNumberController.text.trim(),
        firstName: firstNameController.text.trim(),
        lastName: lastNameController.text.trim(),
        middleName: middleNameController.text.trim().isEmpty
            ? null
            : middleNameController.text.trim(),
        email: emailController.text.trim(),
        phoneNumber: phoneNumberController.text.trim(),

        // Geographic Information
        city: selectedCity,
        state: selectedProvince,
        country: selectedRegion,
        zipCode: zipCodeController.text.trim(),

        // Farm Information
        farmAddress: farmAddressController.text.trim(),
        tenureStatus: selectedTenureStatus,
        farmSize: farmSizeController.text.trim(),
        farmType: selectedFarmType,
        primaryCrop: primaryCropController.text.trim(),
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

  /// Clear messages
  void clearMessages() {
    _errorMessage.value = '';
    _successMessage.value = '';
    _registrationResult.value = null;
  }

  /// Reset form to start over
  void resetForm() {
    _currentStep.value = 1;
    clearMessages();

    // Clear all controllers
    referenceNumberController.clear();
    firstNameController.clear();
    lastNameController.clear();
    middleNameController.clear();
    emailController.clear();
    phoneNumberController.clear();
    zipCodeController.clear();
    farmAddressController.clear();
    farmSizeController.clear();
    primaryCropController.clear();

    // Clear selections
    selectedRegion = '';
    selectedProvince = '';
    selectedCity = '';
    selectedTenureStatus = '';
    selectedFarmType = '';
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
