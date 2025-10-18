import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/presentation/controllers/multi_step_application_controller.dart';
import 'package:mobile/presentation/widgets/application_widgets/application_step_form.dart';
import 'package:mobile/presentation/widgets/common/custom_button.dart';
import 'package:mobile/presentation/widgets/common/step_indicator.dart';

import '../controllers/auth_controller.dart';

/// Multi-step application page
///
/// Displays application form in steps based on sections
/// Handles navigation between steps and final submission
class MultiStepApplicationPage extends ConsumerWidget {
  final ApplicationContent application;

  const MultiStepApplicationPage({
    super.key,
    required this.application,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final controller = MultiStepApplicationController(application);
    controller.initialize();
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        title: Text(application.name),
        backgroundColor: theme.primaryColor,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Column(
        children: [
          // Step indicator
          Container(
            color: Colors.white,
            padding: const EdgeInsets.all(16),
            child: StepIndicator(
              currentStep: controller.currentStep + 1,
              totalSteps: controller.totalSteps,
              stepTitles: application.sections
                  .map((section) => section.title)
                  .toList(),
            ),
          ),

          // Form content
          Expanded(
            child: IndexedStack(
              index: controller.currentStep,
              children: application.sections.asMap().entries.map((entry) {
                final index = entry.key;
                final section = entry.value;

                return ApplicationStepForm(
                  formKey: controller.formKeys[index],
                  section: section,
                  controller: controller,
                );
              }).toList(),
            ),
          ),

          // Navigation buttons
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 10,
                  offset: const Offset(0, -2),
                ),
              ],
            ),
            child: SafeArea(
              child: Row(
                children: [
                  // Previous button
                  if (controller.currentStep != 0)
                    Expanded(
                      child: OutlinedButton(
                        onPressed: controller.isLoading
                            ? null
                            : controller.previousStep,
                        style: OutlinedButton.styleFrom(
                          padding: const EdgeInsets.symmetric(vertical: 16),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        child: const Text('Previous'),
                      ),
                    ),

                  if (controller.currentStep != 0) const SizedBox(width: 16),

                  // Next/Submit button
                  Expanded(
                    flex: 1,
                    child: CustomButton(
                      onPressed: controller.isLoading
                          ? null
                          : (controller.currentStep == controller.totalSteps - 1
                          ? () => controller.submitApplication(context)
                          : controller.nextStep),
                      isLoading: controller.isLoading,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Expanded(
                            child: Text(
                              controller.currentStep == controller.totalSteps - 1
                                  ? 'Submit Application'
                                  : 'Next',
                              style: const TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.w600,
                              ),
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                          const SizedBox(width: 8),
                          Icon(
                            controller.currentStep == controller.totalSteps - 1
                                ? Icons.send
                                : Icons.arrow_forward,
                            size: 20,
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
