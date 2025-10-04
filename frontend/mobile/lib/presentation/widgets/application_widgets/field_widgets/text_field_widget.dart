import 'package:flutter/material.dart';
import '../../../../data/models/application_data.dart';
import '../../common/custom_text_field.dart';

/// Widget for TEXT field type
class TextFieldWidget extends StatelessWidget {
  final ApplicationField field;
  final TextEditingController controller;
  final String? Function(String?)? validator;

  const TextFieldWidget({
    super.key,
    required this.field,
    required this.controller,
    this.validator,
  });

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      controller: controller,
      label: field.fieldName + (field.required ? ' *' : ''),
      prefixIcon: Icons.text_fields_outlined,
      validator: validator,
      maxLines: field.fieldName.toLowerCase().contains('address') ||
              field.fieldName.toLowerCase().contains('boundaries')
          ? 3
          : 1,
    );
  }
}
