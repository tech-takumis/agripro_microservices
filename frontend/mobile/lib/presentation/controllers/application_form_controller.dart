import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:geolocator/geolocator.dart';
import 'package:rflutter_alert/rflutter_alert.dart';
import '../../data/models/application_data.dart';
import '../../data/services/application_api_service.dart';
import '../../data/services/location_service.dart';
import '../../injection_container.dart';

class ApplicationFormController {
  final ApplicationContent application;
  final GlobalKey<FormState> formKey = GlobalKey<FormState>();

  final ValueNotifier<bool> isLoading = ValueNotifier(false);
  final ValueNotifier<String> errorMessage = ValueNotifier('');
  final ValueNotifier<String> successMessage = ValueNotifier('');

  // Dynamic controllers for form fields
  final Map<String, TextEditingController> _textControllers = {};
  final Map<String, XFile?> _fileSelections = {};
  final Map<String, GlobalKey<FormFieldState>> _formFieldKeys = {};

  // Coordinate field, if applicable
  final TextEditingController _coordinateController = TextEditingController();
  final GlobalKey<FormFieldState> _coordinateFieldKey =
      GlobalKey<FormFieldState>();
  final bool _hasCoordinateField;

  ApplicationFormController(this.application)
      : _hasCoordinateField = application.fields.any(
          (field) => field.hasCoordinate,
        ) {
    _initializeControllers();
    clearMessages();
  }

  void _initializeControllers() {
    for (var field in application.fields) {
      final fieldKey = field.key;
      _formFieldKeys[fieldKey] = GlobalKey<FormFieldState>();
      if (field.fieldType == 'TEXT' || field.fieldType == 'NUMBER') {
        _textControllers[fieldKey] = TextEditingController();
        if (field.defaultValue != null && field.defaultValue!.isNotEmpty) {
          _textControllers[fieldKey]!.text = field.defaultValue!;
        }
      } else if (field.fieldType == 'FILE') {
        _fileSelections[fieldKey] = null;
      }
    }
    if (_hasCoordinateField) {
      _formFieldKeys['coordinate'] = _coordinateFieldKey;
    }
  }

  TextEditingController? getTextFieldController(String key) =>
      _textControllers[key];
  XFile? getFileSelection(String key) => _fileSelections[key];
  GlobalKey<FormFieldState>? getFormFieldKey(String key) => _formFieldKeys[key];
  TextEditingController get coordinateController => _coordinateController;
  GlobalKey<FormFieldState> get coordinateFieldKey => _coordinateFieldKey;
  bool get hasCoordinateField => _hasCoordinateField;

  String getFieldKey(ApplicationField field) {
    return field.key;
  }

  Future<void> pickFile(String fieldKey, BuildContext context) async {
    final ImagePicker picker = ImagePicker();
    final XFile? pickedFile = await picker.pickImage(
      source: ImageSource.camera,
    );

    if (pickedFile != null) {
      _fileSelections[fieldKey] = pickedFile;
      getFormFieldKey(fieldKey)?.currentState?.validate();

      // Get GPS coordinates using geolocator if the application has a coordinate field
      if (_hasCoordinateField) {
        try {
          bool locationReady =
              await getIt<LocationService>().checkAndRequestLocationReadiness();

          if (locationReady) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text('Fetching current GPS coordinates...'),
                backgroundColor: Colors.blue.withOpacity(0.8),
                duration: const Duration(seconds: 2),
              ),
            );
            Position position = await Geolocator.getCurrentPosition(
              locationSettings: const LocationSettings(
                accuracy: LocationAccuracy.high,
              ),
            ).timeout(const Duration(seconds: 10));
            final gpsCoordinate =
                '${position.latitude.toStringAsFixed(6)},${position.longitude.toStringAsFixed(6)}';
            _coordinateController.text = gpsCoordinate;
            _coordinateFieldKey.currentState?.validate();
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text('Coordinates: $gpsCoordinate'),
                backgroundColor: Colors.green.withOpacity(0.8),
              ),
            );
          } else {
            _coordinateController.clear();
            _coordinateFieldKey.currentState?.validate();
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(
                  'Please enable location services and grant permissions to capture GPS coordinates.',
                ),
                backgroundColor: Colors.orange.withOpacity(0.8),
                action: SnackBarAction(
                  label: 'Open Settings',
                  textColor: Colors.white,
                  onPressed: () {
                    getIt<LocationService>().openAppSettings();
                  },
                ),
              ),
            );
          }
        } catch (e) {
          print('Error getting GPS data: $e');
          _coordinateController.clear();
          _coordinateFieldKey.currentState?.validate();
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Could not get GPS coordinates: ${e.toString()}'),
              backgroundColor: Colors.red.withOpacity(0.8),
            ),
          );
        }
      }
    }
  }

  void removeFile(String fieldKey) {
    _fileSelections[fieldKey] = null;
    getFormFieldKey(fieldKey)?.currentState?.validate();
    if (_hasCoordinateField) {
      _coordinateController.clear();
      _coordinateFieldKey.currentState?.validate();
    }
  }

  String? validateField(String? value, bool required) {
    if (required && (value == null || value.isEmpty)) {
      return 'This field is required';
    }
    return null;
  }

  String? validateFileField(XFile? file, bool required) {
    if (required && file == null) {
      return 'This file is required';
    }
    return null;
  }

  Future<void> submitForm(BuildContext context) async {
    if (!formKey.currentState!.validate()) {
      errorMessage.value =
          'Please fill in all required fields and select all required files.';
      return;
    }

    try {
      isLoading.value = true;
      errorMessage.value = '';
      successMessage.value = '';

      final Map<String, dynamic> fieldValues = {};
      final Map<String, XFile> filesToUpload = {};

      for (var field in application.fields) {
        final fieldKey = getFieldKey(field);

        if (field.fieldType == 'TEXT') {
          fieldValues[fieldKey] = _textControllers[fieldKey]?.text.trim() ?? '';
        } else if (field.fieldType == 'NUMBER') {
          final text = _textControllers[fieldKey]?.text.trim() ?? '';
          fieldValues[fieldKey] = int.tryParse(text) ?? text;
        } else if (field.fieldType == 'FILE') {
          final file = _fileSelections[fieldKey];
          if (file != null) {
            fieldValues[fieldKey] = file.name;
            filesToUpload[fieldKey] = file;
          } else {
            fieldValues[fieldKey] = '';
          }
        }
      }

      if (_hasCoordinateField && _coordinateController.text.isNotEmpty) {
        fieldValues['coordinate'] = _coordinateController.text.trim();
      }

      final response = await getIt<ApplicationApiService>().submitApplicationFormHttp(
        application.id,
        fieldValues,
        filesToUpload,
      );

      if (!response!.success) {
        throw Exception(response.error ?? response.message);
      }

      successMessage.value = response.message;
      _clearForm();
      _showSuccessAlert(context);
    } catch (e) {
      errorMessage.value = 'An unexpected error occurred: ${e.toString()}';
    } finally {
      isLoading.value = false;
    }
  }

  void _showSuccessAlert(BuildContext context) {
    Alert(
      context: context,
      type: AlertType.success,
      title: "Application Submitted",
      desc: successMessage.value,
      buttons: [
        DialogButton(
          onPressed: () {
            Navigator.of(context).pop(); // Close the alert dialog
            Navigator.of(context).pop(); // Go back to the applications list page
          },
          width: 120,
          child: const Text(
            "OK",
            style: TextStyle(color: Colors.white, fontSize: 20),
          ),
        ),
      ],
    ).show();
  }

  void _clearForm() {
    for (var controller in _textControllers.values) {
      controller.clear();
    }
    for (var key in _fileSelections.keys) {
      _fileSelections[key] = null;
    }
    _coordinateController.clear();
    clearMessages();
  }

  void clearMessages() {
    errorMessage.value = '';
    successMessage.value = '';
  }

  void dispose() {
    for (var controller in _textControllers.values) {
      controller.dispose();
    }
    _coordinateController.dispose();
  }
}
