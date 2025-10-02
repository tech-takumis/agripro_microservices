import 'dart:convert';
import 'package:flutter/material.dart';
import '../../../../data/models/application_data.dart';
import '../../common/custom_text_field.dart';

/// Widget for JSON field type
/// 
/// Provides a multi-line text field for JSON input with validation
class JsonFieldWidget extends StatelessWidget {
  final ApplicationField field;
  final TextEditingController controller;
  final String? Function(String?)? validator;

  const JsonFieldWidget({
    super.key,
    required this.field,
    required this.controller,
    this.validator,
  });

  String? _validateJson(String? value) {
    // First check if required
    if (field.required && (value == null || value.isEmpty)) {
      return 'This field is required';
    }
    
    // If not empty, validate JSON format
    if (value != null && value.isNotEmpty) {
      try {
        jsonDecode(value);
      } catch (e) {
        return 'Invalid JSON format';
      }
    }
    
    return validator?.call(value);
  }

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      controller: controller,
      label: field.fieldName + (field.required ? ' *' : ''),
      prefixIcon: Icons.code,
      maxLines: 5,
      validator: _validateJson,
    );
  }
}
