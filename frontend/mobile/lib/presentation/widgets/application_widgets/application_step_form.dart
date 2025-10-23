import 'package:flutter/material.dart';
import '../../../data/models/application_data.dart';
import '../../controllers/multi_step_application_controller.dart';
import 'field_widgets/text_field_widget.dart';
import 'field_widgets/number_field_widget.dart';
import 'field_widgets/select_field_widget.dart';
import 'field_widgets/date_field_widget.dart';
import 'field_widgets/file_field_widget.dart';
import 'field_widgets/signature_field_widget.dart';
import 'field_widgets/location_field_widget.dart';
import 'field_widgets/boundary_field_widget.dart';
import 'field_widgets/boolean_field_widget.dart';

/// Form widget for a single step/section of the application
/// 
/// Dynamically renders fields based on field types
class ApplicationStepForm extends StatelessWidget {
  final GlobalKey<FormState> formKey;
  final ApplicationSection section;
  final MultiStepApplicationController controller;

  const ApplicationStepForm({
    super.key,
    required this.formKey,
    required this.section,
    required this.controller,
  });

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Form(
        key: formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Section title and description
            Text(
              section.title,
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: Colors.grey[900],
                  ),
            ),
            
            const SizedBox(height: 24),

            // Dynamic fields
            ...section.fields.map((field) {
              return Padding(
                padding: const EdgeInsets.only(bottom: 16),
                child: _buildFieldWidget(field),
              );
            }),
          ],
        ),
      ),
    );
  }

  Widget _buildFieldWidget(ApplicationField field) {
    // Check for special field types first
    if (_isLocationField(field)) {
      return LocationFieldWidget(
        field: field,
        locationValues: controller.getLocationValue(field.key),
        onChanged: (key, value) => controller.setLocationValue(field.key, key, value),
        validator: (values) => controller.validateLocationField(field, values),
      );
    }

    if (_isBoundaryField(field)) {
      return BoundaryFieldWidget(
        field: field,
        controllers: controller.getBoundaryControllers(field.key),
        validator: (values) => controller.validateBoundaryField(field, values),
      );
    }

    // Standard field types
    switch (field.fieldType) {
      case 'TEXT':
        return TextFieldWidget(
          field: field,
          controller: controller.getTextController(field.key)!,
          validator: (value) => controller.validateField(field, value),
        );

      case 'NUMBER':
        return NumberFieldWidget(
          field: field,
          controller: controller.getTextController(field.key)!,
          validator: (value) => controller.validateField(field, value),
        );

      case 'SELECT':
        return SelectFieldWidget(
          field: field,
          initialValue: controller.getSelectValue(field.key),
          onChanged: (value) => controller.setSelectValue(field.key, value),
          validator: (value) => controller.validateField(field, value),
        );

      case 'DATE':
        return DateFieldWidget(
          field: field,
          controller: controller.getTextController(field.key)!,
          validator: (value) => controller.validateField(field, value),
        );

      case 'FILE':
        return FileFieldWidget(
          field: field,
          selectedFile: controller.getFileValue(field.key),
          onFileSelected: (file) => controller.setFileValue(field.key, file),
          validator: (file) => controller.validateField(field, file),
        );

      case 'SIGNATURE':
        return SignatureFieldWidget(
          field: field,
          signatureFile: controller.getSignatureValue(field.key),
          onSignatureChanged: (file) => controller.setSignatureValue(field.key, file),
          validator: (file) => controller.validateField(field, file),
        );

      case 'BOOLEAN':
        return BooleanFieldWidget(
          field: field,
          value: controller.getBooleanValue(field.key),
          onChanged: (value) => controller.setBooleanValue(field.key, value),
          validator: (value) => controller.validateField(field, value),
        );
      default:
        return Text('Unsupported field type: ${field.fieldType}');
    }
  }

  bool _isLocationField(ApplicationField field) {
    return field.key.toLowerCase().contains('location') || 
           field.fieldType.toUpperCase() == 'LOCATION';
  }

  bool _isBoundaryField(ApplicationField field) {
    return field.key.toLowerCase().contains('boundaries') || 
           field.key.toLowerCase().contains('boundary') ||
           field.fieldType.toUpperCase() == 'BOUNDARY';
  }
}
