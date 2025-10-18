import 'package:flutter/material.dart';

import '../controllers/application_form_controller.dart';
import '../../data/models/application_data.dart';
import '../widgets/common/custom_text_field.dart';
import '../widgets/common/custom_button.dart';
import '../../injection_container.dart'; // <-- Add this import

class ApplicationFormPage extends StatefulWidget {
  final ApplicationContent application;

  const ApplicationFormPage({super.key, required this.application});

  @override
  State<ApplicationFormPage> createState() => _ApplicationFormPageState();
}

class _ApplicationFormPageState extends State<ApplicationFormPage> {
  late ApplicationFormController controller;

  @override
  void initState() {
    super.initState();
    controller = ApplicationFormController(widget.application);
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        title: Text(widget.application.displayName),
        backgroundColor: Colors.green[700],
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Form(
            key: controller.formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  widget.application.description,
                  style: Theme.of(
                    context,
                  ).textTheme.titleMedium?.copyWith(color: Colors.grey[700]),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 32),

                // Dynamic fields based on application.fields
                ...widget.application.fields.map((field) {
                  final fieldKey = controller.getFieldKey(field);

                  if (field.fieldType == 'TEXT' ||
                      field.fieldType == 'NUMBER') {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 16.0),
                      child: CustomTextField(
                        key: controller.getFormFieldKey(fieldKey),
                        controller:
                            controller.getTextFieldController(fieldKey)!,
                        label: field.displayName,
                        prefixIcon:
                            field.fieldType == 'NUMBER'
                                ? Icons.numbers
                                : Icons.edit_note,
                        keyboardType:
                            field.fieldType == 'NUMBER'
                                ? TextInputType.number
                                : TextInputType.text,
                        validator:
                            (value) =>
                                controller.validateField(value, field.required),
                      ),
                    );
                  } else if (field.fieldType == 'FILE') {
                    final pickedFile = controller.getFileSelection(fieldKey);
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 16.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            field.displayName,
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.w500,
                              color: Colors.grey[800],
                            ),
                          ),
                          if (field.note.isNotEmpty)
                            Text(
                              field.note,
                              style: TextStyle(
                                fontSize: 12,
                                color: Colors.grey[600],
                              ),
                            ),
                          const SizedBox(height: 8),
                          Container(
                            decoration: BoxDecoration(
                              border: Border.all(color: Colors.grey[300]!),
                              borderRadius: BorderRadius.circular(12),
                              color: Colors.white,
                            ),
                            child: Column(
                              children: [
                                ListTile(
                                  leading: Icon(
                                    pickedFile != null
                                        ? Icons.check_circle
                                        : Icons.camera_alt,
                                    color:
                                        pickedFile != null
                                            ? Colors.green
                                            : Theme.of(context).primaryColor,
                                  ),
                                  title: Text(
                                    pickedFile != null
                                        ? pickedFile.name
                                        : 'Tap to take photo',
                                    style: TextStyle(
                                      color:
                                          pickedFile != null
                                              ? Colors.green[700]
                                              : Colors.grey[700],
                                    ),
                                  ),
                                  trailing:
                                      pickedFile != null
                                          ? IconButton(
                                            icon: const Icon(
                                              Icons.close,
                                              color: Colors.red,
                                            ),
                                            onPressed:
                                                () => setState(() {
                                                  controller.removeFile(
                                                    fieldKey,
                                                  );
                                                }),
                                          )
                                          : null,
                                  onTap: () async {
                                    await controller.pickFile(fieldKey, context);
                                    setState(() {});
                                  },
                                ),
                                // Manual validation message display for file fields
                                if (controller
                                        .getFormFieldKey(fieldKey)
                                        ?.currentState
                                        ?.hasError ==
                                    true)
                                  Padding(
                                    padding: const EdgeInsets.only(
                                      left: 16.0,
                                      right: 16.0,
                                      bottom: 8.0,
                                    ),
                                    child: Align(
                                      alignment: Alignment.centerLeft,
                                      child: Text(
                                        controller
                                                .getFormFieldKey(fieldKey)
                                                ?.currentState
                                                ?.errorText ??
                                            '',
                                        style: TextStyle(
                                          color:
                                              Theme.of(
                                                context,
                                              ).colorScheme.error,
                                          fontSize: 12,
                                        ),
                                      ),
                                    ),
                                  ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    );
                  }
                  return const SizedBox.shrink(); // Fallback for unsupported field types
                }),

                // Coordinate field (if any field requires it)
                if (controller.hasCoordinateField)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 16.0),
                    child: CustomTextField(
                      key: controller.coordinateFieldKey,
                      controller: controller.coordinateController,
                      label: 'Coordinate (Auto-filled from GPS)',
                      prefixIcon: Icons.location_on_outlined,
                      keyboardType: TextInputType.text,
                      readOnly: true,
                      validator:
                          (value) => controller.validateField(value, true),
                    ),
                  ),

                const SizedBox(height: 24),

                // Error Message
                ValueListenableBuilder<String>(
                  valueListenable: controller.errorMessage,
                  builder: (context, error, _) {
                    if (error.isEmpty)
                      return const SizedBox.shrink();
                    return Container(
                      padding: const EdgeInsets.all(16),
                      margin: const EdgeInsets.only(bottom: 16),
                      decoration: BoxDecoration(
                        color: Colors.red[50],
                        border: Border.all(color: Colors.red[300]!),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            Icons.error_outline,
                            color: Colors.red[700],
                            size: 24,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Text(
                              error,
                              style: TextStyle(
                                color: Colors.red[700],
                                fontSize: 14,
                              ),
                            ),
                          ),
                          IconButton(
                            onPressed: controller.clearMessages,
                            icon: Icon(
                              Icons.close,
                              color: Colors.red[700],
                              size: 16,
                            ),
                          ),
                        ],
                      ),
                    );
                  },
                ),

                // Submit Button
                ValueListenableBuilder<bool>(
                  valueListenable: controller.isLoading,
                  builder: (context, isLoading, _) => CustomButton(
                    onPressed:
                        isLoading ? null : () => controller.submitForm(context),
                    isLoading: isLoading,
                    child: const Text(
                      'Submit Application',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 24),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
