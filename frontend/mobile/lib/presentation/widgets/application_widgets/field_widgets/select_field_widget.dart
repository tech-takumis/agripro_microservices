import 'package:flutter/material.dart';
import '../../../../data/models/application_data.dart';
import '../../common/custom_dropdown.dart';

/// Widget for SELECT field type
class SelectFieldWidget extends StatefulWidget {
  final ApplicationField field;
  final String? initialValue;
  final void Function(String?) onChanged;
  final String? Function(String?)? validator;

  const SelectFieldWidget({
    super.key,
    required this.field,
    this.initialValue,
    required this.onChanged,
    this.validator,
  });

  @override
  State<SelectFieldWidget> createState() => _SelectFieldWidgetState();
}

class _SelectFieldWidgetState extends State<SelectFieldWidget> {
  String? _selectedValue;

  @override
  void initState() {
    super.initState();
    _selectedValue = widget.initialValue;
  }

  @override
  Widget build(BuildContext context) {
    return CustomDropdown<String>(
      label: widget.field.fieldName + (widget.field.required ? ' *' : ''),
      value: _selectedValue,
      items: widget.field.choices ?? [],
      displayText: (value) => value,
      onChanged: (value) {
        setState(() {
          _selectedValue = value;
        });
        widget.onChanged(value);
      },
      validator: widget.validator,
      prefixIcon: Icons.arrow_drop_down_circle,
      hint: 'Select ${widget.field.fieldName}',
    );
  }
}
