import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart'; 
import 'package:mobile/data/services/storage_service.dart';
import 'package:geolocator/geolocator.dart'; 
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/data/models/application_submission.dart';
import 'package:mobile/data/services/application_api_service.dart';
import 'package:mobile/injection_container.dart';


/// Handles step navigation, field values, file uploads, and submission
class MultiStepApplicationController {
  final ApplicationContent application;

  MultiStepApplicationController(this.application);

  // Current step tracking
  int _currentStep = 0;
  int get currentStep => _currentStep;
  int get totalSteps => application.sections.length;

  // Form keys for each step
  late final List<GlobalKey<FormState>> formKeys;

  // Field values storage
  final Map<String, TextEditingController> _textControllers = {};
  final Map<String, String?> _selectValues = {};
  final Map<String, PlatformFile?> _fileValues = {};
  final Map<String, bool?> _booleanValues = {};

  // Location field values (for PSGC location fields)
  final Map<String, Map<String, String?>> _locationValues = {};

  // Boundary field values (north, south, east, west)
  final Map<String, Map<String, TextEditingController>> _boundaryControllers = {};

  // Loading and error states
  bool _isLoading = false;
  String _errorMessage = '';
  double _uploadProgress = 0.0;

  bool get isLoading => _isLoading;
  String get errorMessage => _errorMessage;
  double get uploadProgress => _uploadProgress;

  void initialize() {
    _initializeFormKeys();
    _initializeControllers();
  }

  void _initializeFormKeys() {
    formKeys = List.generate(
      totalSteps,
      (index) => GlobalKey<FormState>(),
    );
  }

  void _initializeControllers() {
    // Get user credentials from storage
    final userCredentials = getIt<StorageService>().getUserCredentials();
    final user = userCredentials?.user;
    final profile = user?.profile;

    // Helper to get user/profile values by field key
    String? getUserFieldValue(String key) {
      final lowerKey = key.toLowerCase();
      // Common mappings (expand as needed)
      if (lowerKey == 'first_name' || lowerKey == 'firstname') return user?.firstName;
      if (lowerKey == 'last_name' || lowerKey == 'lastname') return user?.lastName;
      if (lowerKey == 'middle_name' || lowerKey == 'middlename') return null; // Add if available
      if (lowerKey == 'email') return user?.email;
      if (lowerKey == 'cell_phone_number' || lowerKey == 'phone' || lowerKey == 'phone_number') return user?.phoneNumber;
      if (lowerKey == 'sex' || lowerKey == 'gender') return profile?.gender;
      if (lowerKey == 'date_of_birth' || lowerKey == 'dob') return profile?.dateOfBirth;
      if (lowerKey == 'civil_status') return profile?.civilStatus;
      if (lowerKey == 'address') {
        final parts = [
          profile?.houseNo,
          profile?.street,
          profile?.barangay,
          profile?.municipality,
          profile?.province
        ].where((e) => e != null && e.isNotEmpty).toList();
        return parts.isNotEmpty ? parts.join(', ') : null;
      }
      // Add more mappings as needed
      return null;
    }

    for (var section in application.sections) {
      for (var field in section.fields) {
        // Check if field is a location field (by key pattern)
        if (_isLocationField(field)) {
          _locationValues[field.key] = {
            'region': null,
            'province': null,
            'city': null,
            'barangay': null,
          };
        }
        // Check if field is a boundary field (by key pattern)
        else if (_isBoundaryField(field)) {
          _boundaryControllers[field.key] = {
            'north': TextEditingController(),
            'south': TextEditingController(),
            'east': TextEditingController(),
            'west': TextEditingController(),
          };
        }
        // Standard field types
        else if (field.fieldType == 'TEXT' ||
            field.fieldType == 'NUMBER' ||
            field.fieldType == 'DATE') {
          // Use user/profile value if available, else defaultValue
          final userValue = getUserFieldValue(field.key);
          _textControllers[field.key] = TextEditingController(
            text: userValue ?? field.defaultValue,
          );
        } else if (field.fieldType == 'SELECT') {
          _selectValues[field.key] = field.defaultValue;
        } else if (field.fieldType == 'FILE' || field.fieldType == 'SIGNATURE') {
          _fileValues[field.key] = null;
        } else if (field.fieldType == 'BOOLEAN') {
          _booleanValues[field.key] = field.defaultValue == 'true'; // defaultValue as string
        }
      }
    }
  }

  // Helper methods to identify special field types
  bool _isLocationField(ApplicationField field) {
    return field.key.toLowerCase().contains('location') ||
        field.fieldType.toUpperCase() == 'LOCATION';
  }

  bool _isBoundaryField(ApplicationField field) {
    return field.key.toLowerCase().contains('boundaries') ||
        field.key.toLowerCase().contains('boundary') ||
        field.fieldType.toUpperCase() == 'BOUNDARY';
  }

  // Getters for field values
  TextEditingController? getTextController(String key) => _textControllers[key];

  String? getSelectValue(String key) => _selectValues[key];

  void setSelectValue(String key, String? value) {
    _selectValues[key] = value;
  }

  PlatformFile? getFileValue(String key) => _fileValues[key];

  void setFileValue(String key, PlatformFile? file) {
    _fileValues[key] = file;
  }

  // Add these for SIGNATURE fields (same as FILE)
  PlatformFile? getSignatureValue(String key) => _fileValues[key];

  void setSignatureValue(String key, PlatformFile? file) {
    _fileValues[key] = file;
  }

  // Boolean field getters/setters
  bool? getBooleanValue(String key) => _booleanValues[key];
  void setBooleanValue(String key, bool? value) {
    _booleanValues[key] = value;
  }

  // Location field getters/setters
  Map<String, String?> getLocationValue(String key) {
    return _locationValues[key] ?? {};
  }

  void setLocationValue(String fieldKey, String locationKey, String? value) {
    if (_locationValues[fieldKey] == null) {
      _locationValues[fieldKey] = {};
    }
    _locationValues[fieldKey]![locationKey] = value;
  }

  // Boundary field getters
  Map<String, TextEditingController> getBoundaryControllers(String key) {
    return _boundaryControllers[key] ?? {};
  }

  // Validation
  String? validateField(ApplicationField field, dynamic value) {
    if (field.required) {
      if (value == null || (value is String && value.isEmpty)) {
        return 'This field is required';
      }
      if (field.fieldType == 'BOOLEAN' && value == null) {
        return 'This field is required';
      }
      if ((field.fieldType == 'FILE' || field.fieldType == 'SIGNATURE') && value == null) {
        return 'This field is required';
      }
    }

    if (field.validationRegex != null && value is String && value.isNotEmpty) {
      final regex = RegExp(field.validationRegex!);
      if (!regex.hasMatch(value)) {
        return 'Invalid format';
      }
    }

    if (field.fieldType == 'NUMBER' && value is String && value.isNotEmpty) {
      if (num.tryParse(value) == null) {
        return 'Field must be a number value (NUMBER)';
      }
    }

    return null;
  }

  String? validateLocationField(ApplicationField field, Map<String, String?> locationValues) {
    if (field.required) {
      if (locationValues['region'] == null || locationValues['region']!.isEmpty) {
        return 'Please select a region';
      }
      if (locationValues['province'] == null || locationValues['province']!.isEmpty) {
        return 'Please select a province';
      }
      if (locationValues['city'] == null || locationValues['city']!.isEmpty) {
        return 'Please select a city/municipality';
      }
      if (locationValues['barangay'] == null || locationValues['barangay']!.isEmpty) {
        return 'Please select a barangay';
      }
    }
    return null;
  }

  String? validateBoundaryField(ApplicationField field, Map<String, String>? boundaryValues) {
    if (field.required && boundaryValues != null) {
      if (boundaryValues['north']?.isEmpty ?? true) {
        return 'All boundaries are required';
      }
      if (boundaryValues['south']?.isEmpty ?? true) {
        return 'All boundaries are required';
      }
      if (boundaryValues['east']?.isEmpty ?? true) {
        return 'All boundaries are required';
      }
      if (boundaryValues['west']?.isEmpty ?? true) {
        return 'All boundaries are required';
      }
    }
    return null;
  }

  // Step navigation
  void nextStep() {
    if (_validateCurrentStep()) {
      if (_currentStep < totalSteps - 1) {
        _currentStep++;
      }
    }
  }

  void previousStep() {
    if (_currentStep > 0) {
      _currentStep--;
    }
  }

  bool _validateCurrentStep() {
    return formKeys[_currentStep].currentState?.validate() ?? false;
  }

  // Submission
  Future<ApplicationSubmissionResponse> submitApplication(AuthState authState) async {
    if (!_validateCurrentStep()) {
      _errorMessage = 'Please fill in all required fields';
      _isLoading = false;
      return ApplicationSubmissionResponse(success: false, message: _errorMessage, applicationId: '');
    }

    try {
      _isLoading = true;
      _errorMessage = '';
      _uploadProgress = 0.0;

      // Collect files (no need to upload one by one)
      final fileEntries = _fileValues.entries.where((entry) => entry.value != null);

      // Ensure required signature field is present in files
      for (var section in application.sections) {
        for (var field in section.fields) {
          if (field.fieldType == 'SIGNATURE' && field.required) {
            final file = _fileValues[field.key];
            if (file == null) {
              _errorMessage = "Signature field '${field.fieldName}' is required.";
              return ApplicationSubmissionResponse(success: false, message: _errorMessage, applicationId: '');
            }
          }
        }
      }

      // Prepare field values
      final Map<String, dynamic> fieldValues = {};
      for (var section in application.sections) {
        for (var field in section.fields) {
          final key = field.key;
          if (_isLocationField(field)) {
            final location = _locationValues[key];
            if (location != null) {
              fieldValues[key] = {
                'region': location['region'] ?? '',
                'province': location['province'] ?? '',
                'city': location['city'] ?? '',
                'barangay': location['barangay'] ?? '',
              };
            }
          } else if (_isBoundaryField(field)) {
            final controllers = _boundaryControllers[key];
            if (controllers != null) {
              fieldValues[key] = {
                'north': controllers['north']?.text ?? '',
                'south': controllers['south']?.text ?? '',
                'east': controllers['east']?.text ?? '',
                'west': controllers['west']?.text ?? '',
              };
            }
          } else if (field.fieldType == 'TEXT') {
            fieldValues[key] = _textControllers[key]?.text ?? '';
          } else if (field.fieldType == 'DATE') {
            final rawDate = _textControllers[key]?.text ?? '';
            if (rawDate.isEmpty) {
              fieldValues[key] = '';
            } else {
              try {
                final parsedDate = DateTime.parse(rawDate);
                fieldValues[key] = "${parsedDate.year.toString().padLeft(4, '0')}-"
                                   "${parsedDate.month.toString().padLeft(2, '0')}-"
                                   "${parsedDate.day.toString().padLeft(2, '0')}";
              } catch (e) {
                fieldValues[key] = rawDate;
              }
            }
          } else if (field.fieldType == 'NUMBER') {
            final text = _textControllers[key]?.text ?? '';
            if (text.isEmpty) {
              fieldValues[key] = null;
            } else {
              final parsed = num.tryParse(text);
              fieldValues[key] = parsed ?? text;
            }
          } else if (field.fieldType == 'SELECT') {
            fieldValues[key] = _selectValues[key] ?? '';
          } else if (field.fieldType == 'FILE') {
            fieldValues[key] = null; // No pre-uploaded docs
          } else if (field.fieldType == 'SIGNATURE') {
            fieldValues[key] = null; // No pre-uploaded docs
          } else if (field.fieldType == 'BOOLEAN') {
            fieldValues[key] = _booleanValues[key] ?? false;
          }
        }
      }

      // Prepare request
      // Get coordinates before submission (replace with your actual logic)
      final coordinates = await _getCoordinates();

      final request = ApplicationSubmissionRequest(
        applicationTypeId: application.id,
        fieldValues: fieldValues,
        coordinates: coordinates,
      );

      // Pass all files to the API service
      final response = await getIt<ApplicationApiService>().submitApplication(
        authState,
        request,
        fileEntries.map((e) => e.value!).toList(),
      );

      _isLoading = false;
      print('✅ Application submission completed: ${response.success}');
      return response;
    } catch (e) {
      _isLoading = false;
      _errorMessage = 'Failed to submit application: $e';
      print('❌ Failed to submit application: $e');
      return ApplicationSubmissionResponse(success: false, message: _errorMessage, applicationId: '');
    } finally {
      _isLoading = false;
    }
  }

  // Dummy coordinates getter (replace with your actual logic)
  Future<String> _getCoordinates() async {
    try {
      // Request location permission if not already granted
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied || permission == LocationPermission.deniedForever) {
        permission = await Geolocator.requestPermission();
      }

      // If permission is granted, get current position
      if (permission == LocationPermission.whileInUse || permission == LocationPermission.always) {
        final position = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
        return "${position.latitude},${position.longitude}";
      } else {
        // Permission not granted, return default or empty coordinates
        return "0.00000,0.00000";
      }
    } catch (e) {
      print("Error getting coordinates: $e");
      return "0.00000,0.00000";
    }
  }

  void clearError() {
    _errorMessage = '';
  }

  void dispose() {
    for (var controller in _textControllers.values) {
      controller.dispose();
    }
    for (var controllers in _boundaryControllers.values) {
      for (var controller in controllers.values) {
        controller.dispose();
      }
    }
  }
}
