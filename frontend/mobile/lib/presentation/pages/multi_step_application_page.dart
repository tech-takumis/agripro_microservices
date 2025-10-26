import 'package:another_flushbar/flushbar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/features/messages/providers/router_provider.dart';
import 'package:mobile/presentation/controllers/multi_step_application_controller.dart';
import 'package:mobile/presentation/widgets/application_widgets/application_step_form.dart';
import 'package:mobile/presentation/widgets/common/custom_button.dart';
import 'package:mobile/presentation/widgets/common/step_indicator.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';

class MultiStepApplicationPage extends ConsumerStatefulWidget {
  final ApplicationContent application;

  const MultiStepApplicationPage({
    super.key,
    required this.application,
  });

  @override
  ConsumerState<MultiStepApplicationPage> createState() => _MultiStepApplicationPageState();
}

class _MultiStepApplicationPageState extends ConsumerState<MultiStepApplicationPage> {

  Future<void> _submitApplication(WidgetRef ref, BuildContext context, AuthState authState) async {

    final response = await controller.submitApplication(authState);
    if (!context.mounted) return;

    if (response.success) {
      await Flushbar(
        message: response.message.isNotEmpty
            ? response.message
            : 'Application submitted successfully!',
        backgroundColor: Colors.green.withOpacity(0.9),
        duration: const Duration(seconds: 2),
        margin: const EdgeInsets.all(12),
        borderRadius: BorderRadius.circular(8),
        flushbarPosition: FlushbarPosition.TOP,
      ).show(context);

      if (context.mounted) {
        ref.read(goRouterProvider).go('/home');
      }
    } else {
      if (!context.mounted) return;
      Flushbar(
        message: response.message,
        backgroundColor: Colors.red.withOpacity(0.9),
        duration: const Duration(seconds: 4),
      ).show(context);
    }
  }


  late final MultiStepApplicationController controller;

  @override
  void initState() {
    super.initState();
    controller = MultiStepApplicationController(widget.application);
    controller.initialize();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authProvider);
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        title: Text(widget.application.name),
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
              stepTitles: widget.application.sections
                  .map((section) => section.title)
                  .toList(),
            ),
          ),

          // Form content
          Expanded(
            child: IndexedStack(
              index: controller.currentStep,
              children: widget.application.sections.asMap().entries.map((entry) {
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
                  color: const Color.fromARGB(0, 0, 0, 0).withAlpha(204),
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
                            : () {
                                setState(() {
                                  controller.previousStep();
                                });
                              },
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
                              ? () => _submitApplication(ref,context, authState)
                              : () {
                                  setState(() {
                                    controller.nextStep();
                                  });
                                }),
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
