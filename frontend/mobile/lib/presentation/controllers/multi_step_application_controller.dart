import 'dart:io';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import '../../data/models/application_data.dart';
import '../../data/models/application_submission_request.dart';
import '../../data/services/document_service.dart';
import '../../data/services/api_service.dart';

/// Controller for multi-step application submission
///
/// Handles step navigation, field values, file uploads, and submission
class MultiStepApplicationController extends GetxController {
  final ApplicationContent application;

  MultiStepApplicationController(this.application);

  // Current step tracking
  final _currentStep = 0.obs;
  int get currentStep => _currentStep.value;
  int get totalSteps => application.sections.length;

  // Form keys for each step
  late final List<GlobalKey<FormState>> formKeys;

  // Field values storage
  final Map<String, TextEditingController> _textControllers = {};
  final Map<String, String?> _selectValues = {};
  final Map<String, XFile?> _fileValues = {};
  final Map<String, bool?> _booleanValues = {}; // <-- Add boolean field storage

  // Location field values (for PSGC location fields)
  final Map<String, Map<String, String?>> _locationValues = {};

  // Boundary field values (north, south, east, west)
  final Map<String, Map<String, TextEditingController>> _boundaryControllers = {};

  // Loading and error states
  final _isLoading = false.obs;
  final _errorMessage = ''.obs;
  final _uploadProgress = 0.0.obs;

  bool get isLoading => _isLoading.value;
  String get errorMessage => _errorMessage.value;
  double get uploadProgress => _uploadProgress.value;

  @override
  void onInit() {
    super.onInit();
    // Ensure DocumentService is registered
    if (!Get.isRegistered<DocumentService>()) {
      Get.put(DocumentService());
    }
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
          _textControllers[field.key] = TextEditingController(
            text: field.defaultValue,
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
    // Check if the field key contains 'location' or if fieldType is 'LOCATION'
    return field.key.toLowerCase().contains('location') ||
        field.fieldType.toUpperCase() == 'LOCATION';
  }

  bool _isBoundaryField(ApplicationField field) {
    // Check if the field key contains 'boundaries' or 'boundary'
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

  XFile? getFileValue(String key) => _fileValues[key];

  void setFileValue(String key, XFile? file) {
    _fileValues[key] = file;
    update();
  }

  // Boolean field getters/setters
  bool? getBooleanValue(String key) => _booleanValues[key];
  void setBooleanValue(String key, bool? value) {
    _booleanValues[key] = value;
    update();
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
    update();
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
      // Add validation for FILE and SIGNATURE fields
      if ((field.fieldType == 'FILE' || field.fieldType == 'SIGNATURE') && value == null) {
        return 'This field is required';
      }
    }

    // Additional validation based on field type
    if (field.validationRegex != null && value is String && value.isNotEmpty) {
      final regex = RegExp(field.validationRegex!);
      if (!regex.hasMatch(value)) {
        return 'Invalid format';
      }
    }

    // NUMBER field validation
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
      if (_currentStep.value < totalSteps - 1) {
        _currentStep.value++;
      }
    }
  }

  void previousStep() {
    if (_currentStep.value > 0) {
      _currentStep.value--;
    }
  }

  bool _validateCurrentStep() {
    return formKeys[_currentStep.value].currentState?.validate() ?? false;
  }

  // Submission
  Future<void> submitApplication() async {
    if (!_validateCurrentStep()) {
      _errorMessage.value = 'Please fill in all required fields';
      return;
    }

    try {
      _isLoading.value = true;
      _errorMessage.value = '';
      _uploadProgress.value = 0.0;

      // Step 1: Upload all files and signatures first
      final List<String> documentIds = [];
      final Map<String, String> fieldDocumentIds = {}; // <-- Map fieldKey to documentId
      final filesToUpload = _fileValues.entries
          .where((entry) => entry.value != null)
          .toList();

      if (filesToUpload.isNotEmpty) {
        Get.snackbar(
          'Uploading Documents',
          'Uploading ${filesToUpload.length} document(s)...',
          snackPosition: SnackPosition.BOTTOM,
          backgroundColor: Colors.blue.withOpacity(0.8),
          colorText: Colors.white,
          duration: const Duration(seconds: 2),
        );

        for (var i = 0; i < filesToUpload.length; i++) {
          final entry = filesToUpload[i];
          final fieldKey = entry.key;
          final file = entry.value!;

          try {
            final documentResponse = await DocumentService.to.uploadDocument(
              referenceId: application.id,
              file: File(file.path),
              documentType: fieldKey,
            );

            documentIds.add(documentResponse.documentId);
            fieldDocumentIds[fieldKey] = documentResponse.documentId; // <-- Save mapping
            _uploadProgress.value = (i + 1) / filesToUpload.length;
          } catch (e) {
            throw Exception('Failed to upload $fieldKey: $e');
          }
        }
      }

      // Step 2: Prepare field values
      final Map<String, dynamic> fieldValues = {};

      for (var section in application.sections) {
        for (var field in section.fields) {
          final key = field.key;

          // Handle location fields
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
          }
          // Handle boundary fields
          else if (_isBoundaryField(field)) {
            final controllers = _boundaryControllers[key];
            if (controllers != null) {
              fieldValues[key] = {
                'north': controllers['north']?.text ?? '',
                'south': controllers['south']?.text ?? '',
                'east': controllers['east']?.text ?? '',
                'west': controllers['west']?.text ?? '',
              };
            }
          }
          // Standard field types
          else if (field.fieldType == 'TEXT' || field.fieldType == 'DATE') {
            fieldValues[key] = _textControllers[key]?.text ?? '';
          } else if (field.fieldType == 'NUMBER') {
            final text = _textControllers[key]?.text ?? '';
            // Only assign number if not empty and valid
            if (text.isEmpty) {
              fieldValues[key] = null;
            } else {
              final parsed = num.tryParse(text);
              fieldValues[key] = parsed ?? text;
            }
          } else if (field.fieldType == 'SELECT') {
            fieldValues[key] = _selectValues[key] ?? '';
          } else if (field.fieldType == 'FILE') {
            // Use documentId if uploaded, else null
            fieldValues[key] = fieldDocumentIds.containsKey(key)
                ? fieldDocumentIds[key]
                : null;
          } else if (field.fieldType == 'SIGNATURE') {
            // For signature fields, add 'signature:' prefix to the document ID
            fieldValues[key] = fieldDocumentIds.containsKey(key)
                ? 'signature:${fieldDocumentIds[key]}'
                : null;
          } else if (field.fieldType == 'BOOLEAN') {
            fieldValues[key] = _booleanValues[key] ?? false;
          }
        }
      }

      // Step 3: Submit application
      final request = ApplicationSubmissionRequest(
        applicationTypeId: application.id,
        fieldValues: fieldValues,
        documentIds: documentIds,
      );

      final response = await ApiService.to.submitApplication(request);

      if (response.success) {
        Get.snackbar(
          'Success',
          response.message,
          snackPosition: SnackPosition.BOTTOM,
          backgroundColor: Colors.green.withOpacity(0.8),
          colorText: Colors.white,
          duration: const Duration(seconds: 3),
        );

        // Navigate back to applications list
        Get.back();
        Get.back();
      } else {
        throw Exception(response.message);
      }
    } catch (e) {
      _errorMessage.value = e.toString().replaceFirst('Exception: ', '');
      Get.snackbar(
        'Error',
        _errorMessage.value,
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: Colors.red.withOpacity(0.8),
        colorText: Colors.white,
        duration: const Duration(seconds: 4),
      );
    } finally {
      _isLoading.value = false;
      _uploadProgress.value = 0.0;
    }
  }

  void clearError() {
    _errorMessage.value = '';
  }

  @override
  void onClose() {
    // Dispose text controllers
    for (var controller in _textControllers.values) {
      controller.dispose();
    }
    // Dispose boundary controllers
    for (var controllers in _boundaryControllers.values) {
      for (var controller in controllers.values) {
        controller.dispose();
      }
    }
    super.onClose();
  }
}
