import 'package:flutter/material.dart';
import '../../../../data/models/application_data.dart';

class BooleanFieldWidget extends StatelessWidget {
  final ApplicationField field;
  final bool? value;
  final ValueChanged<bool?> onChanged;
  final FormFieldValidator<bool?>? validator;

  const BooleanFieldWidget({
    super.key,
    required this.field,
    required this.value,
    required this.onChanged,
    this.validator,
  });

  @override
  Widget build(BuildContext context) {
    return FormField<bool>(
      initialValue: value ?? false,
      validator: validator,
      builder: (state) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Switch(
                  value: state.value ?? false,
                  onChanged: (val) {
                    state.didChange(val);
                    onChanged(val);
                  },
                ),
                Expanded(
                  child: Text(
                    field.fieldName,
                    style: Theme.of(context).textTheme.bodyLarge,
                  ),
                ),
              ],
            ),
            if (state.hasError)
              Padding(
                padding: const EdgeInsets.only(left: 8.0, top: 4.0),
                child: Text(
                  state.errorText ?? '',
                  style: TextStyle(color: Theme.of(context).colorScheme.error),
                ),
              ),
          ],
        );
      },
    );
  }
}

