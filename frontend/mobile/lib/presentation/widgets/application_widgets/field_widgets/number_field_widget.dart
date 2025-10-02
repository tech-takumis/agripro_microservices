import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../../../data/models/application_data.dart';
import '../../common/custom_text_field.dart';

/// Widget for NUMBER field type
class NumberFieldWidget extends StatelessWidget {
  final ApplicationField field;
  final TextEditingController controller;
  final String? Function(String?)? validator;

  const NumberFieldWidget({
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
      prefixIcon: Icons.numbers,
      keyboardType: const TextInputType.numberWithOptions(decimal: true),
      inputFormatters: [
        FilteringTextInputFormatter.allow(RegExp(r'^\d+\.?\d{0,2}')),
      ],
      validator: validator,
    );
  }
}
