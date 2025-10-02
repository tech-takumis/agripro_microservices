import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../../../data/models/application_data.dart';
import '../../common/custom_text_field.dart';

/// Widget for DATE field type
class DateFieldWidget extends StatefulWidget {
  final ApplicationField field;
  final TextEditingController controller;
  final String? Function(String?)? validator;

  const DateFieldWidget({
    super.key,
    required this.field,
    required this.controller,
    this.validator,
  });

  @override
  State<DateFieldWidget> createState() => _DateFieldWidgetState();
}

class _DateFieldWidgetState extends State<DateFieldWidget> {
  final DateFormat _dateFormat = DateFormat('yyyy-MM-dd');

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(1900),
      lastDate: DateTime(2100),
      builder: (context, child) {
        return Theme(
          data: Theme.of(context).copyWith(
            colorScheme: ColorScheme.light(
              primary: Theme.of(context).primaryColor,
            ),
          ),
          child: child!,
        );
      },
    );

    if (picked != null) {
      setState(() {
        widget.controller.text = _dateFormat.format(picked);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      controller: widget.controller,
      label: widget.field.fieldName + (widget.field.required ? ' *' : ''),
      prefixIcon: Icons.calendar_today,
      readOnly: true,
      validator: widget.validator,
      suffixIcon: IconButton(
        icon: const Icon(Icons.calendar_month),
        onPressed: () => _selectDate(context),
      ),
    );
  }
}
