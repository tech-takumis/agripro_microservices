import 'package:flutter/material.dart';
import '../../../../data/models/application_data.dart';
import '../../common/custom_text_field.dart';

/// Widget for boundary input (North, South, East, West)
class BoundaryFieldWidget extends StatefulWidget {
  final ApplicationField field;
  final Map<String, TextEditingController> controllers;
  final String? Function(Map<String, String>?)? validator;

  const BoundaryFieldWidget({
    super.key,
    required this.field,
    required this.controllers,
    this.validator,
  });

  @override
  State<BoundaryFieldWidget> createState() => _BoundaryFieldWidgetState();
}

class _BoundaryFieldWidgetState extends State<BoundaryFieldWidget> {
  @override
  Widget build(BuildContext context) {
    final hasError = widget.validator?.call(_getBoundaryValues()) != null;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.field.fieldName + (widget.field.required ? ' *' : ''),
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w500,
            color: Colors.grey[800],
          ),
        ),
        const SizedBox(height: 12),

        // North
        CustomTextField(
          controller: widget.controllers['north']!,
          label: 'North Boundary',
          prefixIcon: Icons.arrow_upward,
          validator: (value) {
            if (widget.field.required && (value == null || value.isEmpty)) {
              return 'North boundary is required';
            }
            return null;
          },
        ),

        const SizedBox(height: 12),

        // South
        CustomTextField(
          controller: widget.controllers['south']!,
          label: 'South Boundary',
          prefixIcon: Icons.arrow_downward,
          validator: (value) {
            if (widget.field.required && (value == null || value.isEmpty)) {
              return 'South boundary is required';
            }
            return null;
          },
        ),

        const SizedBox(height: 12),

        // East
        CustomTextField(
          controller: widget.controllers['east']!,
          label: 'East Boundary',
          prefixIcon: Icons.arrow_forward,
          validator: (value) {
            if (widget.field.required && (value == null || value.isEmpty)) {
              return 'East boundary is required';
            }
            return null;
          },
        ),

        const SizedBox(height: 12),

        // West
        CustomTextField(
          controller: widget.controllers['west']!,
          label: 'West Boundary',
          prefixIcon: Icons.arrow_back,
          validator: (value) {
            if (widget.field.required && (value == null || value.isEmpty)) {
              return 'West boundary is required';
            }
            return null;
          },
        ),

        if (hasError)
          Padding(
            padding: const EdgeInsets.only(left: 16, top: 8),
            child: Text(
              widget.validator!.call(_getBoundaryValues())!,
              style: TextStyle(
                color: Theme.of(context).colorScheme.error,
                fontSize: 12,
              ),
            ),
          ),
      ],
    );
  }

  Map<String, String> _getBoundaryValues() {
    return {
      'north': widget.controllers['north']?.text ?? '',
      'south': widget.controllers['south']?.text ?? '',
      'east': widget.controllers['east']?.text ?? '',
      'west': widget.controllers['west']?.text ?? '',
    };
  }
}