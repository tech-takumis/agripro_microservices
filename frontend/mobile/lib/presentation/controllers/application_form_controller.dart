import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:geolocator/geolocator.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/data/services/location_service.dart';
import 'package:mobile/injection_container.dart';

class ApplicationFormController {
  final ApplicationContent application;
  final GlobalKey<FormState> formKey = GlobalKey<FormState>();

  final ValueNotifier<bool> isLoading = ValueNotifier(false);
  final ValueNotifier<String> errorMessage = ValueNotifier('');
  final ValueNotifier<String> successMessage = ValueNotifier('');

  // Dynamic controllers for form fields
  final Map<String, TextEditingController> _textControllers = {};
  final Map<String, PlatformFile?> _fileValues = {}; // For FILE fields
  final Map<String, XFile?> _signatureValues = {}; // For SIGNATURE fields
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
        _fileValues[fieldKey] = null;
      } else if (field.fieldType == 'SIGNATURE') {
        _signatureValues[fieldKey] = null;
      }
    }
    if (_hasCoordinateField) {
      _formFieldKeys['coordinate'] = _coordinateFieldKey;
    }
  }

  TextEditingController? getTextFieldController(String key) =>
      _textControllers[key];
  PlatformFile? getFileValue(String key) => _fileValues[key];
  XFile? getSignatureValue(String key) => _signatureValues[key];
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
      _signatureValues[fieldKey] = pickedFile;
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
    _signatureValues[fieldKey] = null;
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

  String? validateFileField(PlatformFile? file, bool required) {
    if (required && file == null) {
      return 'This file is required';
    }
    return null;
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
