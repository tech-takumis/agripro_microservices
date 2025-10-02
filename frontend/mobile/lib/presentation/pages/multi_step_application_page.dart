import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/presentation/controllers/multi_step_application_controller.dart';
import 'package:mobile/presentation/widgets/common/step_indicator.dart';
import 'package:mobile/presentation/widgets/common/custom_button.dart';
import 'package:mobile/presentation/widgets/application_widgets/application_step_form.dart';

/// Multi-step application page
///
/// Displays application form in steps based on sections
/// Handles navigation between steps and final submission
class MultiStepApplicationPage extends StatelessWidget {
  final ApplicationContent application;

  const MultiStepApplicationPage({
    super.key,
    required this.application,
  });

  @override
  Widget build(BuildContext context) {
    final controller = Get.put(
      MultiStepApplicationController(application),
    );
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
            child: Obx(
                  () => StepIndicator(
                currentStep: controller.currentStep + 1,
                totalSteps: controller.totalSteps,
                stepTitles: application.sections
                    .map((section) => section.title)
                    .toList(),
              ),
            ),
          ),

          // Form content
          Expanded(
            child: Obx(
                  () => IndexedStack(
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
              child: Obx(() {
                final isFirstStep = controller.currentStep == 0;
                final isLastStep =
                    controller.currentStep == controller.totalSteps - 1;

                return Row(
                  children: [
                    // Previous button
                    if (!isFirstStep)
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

                    if (!isFirstStep) const SizedBox(width: 16),

                    // Next/Submit button
                    Expanded(
                      flex: isFirstStep ? 1 : 1,
                      child: CustomButton(
                        onPressed: controller.isLoading
                            ? null
                            : (isLastStep
                            ? controller.submitApplication
                            : controller.nextStep),
                        isLoading: controller.isLoading,
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Expanded(
                              child: Text(
                                isLastStep ? 'Submit Application' : 'Next',
                                style: const TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w600,
                                ),
                                overflow: TextOverflow.ellipsis,
                              ),
                            ),
                            const SizedBox(width: 8),
                            Icon(
                              isLastStep ? Icons.send : Icons.arrow_forward,
                              size: 20,
                            ),
                          ],
                        ),
                      ),
                    ),
                  ],
                );
              }),
            ),
          ),
        ],
      ),
    );
  }
}
