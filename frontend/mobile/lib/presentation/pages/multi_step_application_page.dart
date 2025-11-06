import 'dart:async';
import 'package:another_flushbar/flushbar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:mobile/presentation/controllers/multi_step_application_controller.dart';
import 'package:mobile/presentation/widgets/application_widgets/application_step_form.dart';
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
  late final MultiStepApplicationController controller;

  Future<void> _submitApplication(BuildContext context, AuthState authState) async {
    final response = await controller.submitApplication(authState);

    if (!context.mounted) return;

    if (response.success) {
      print('🚀 Application submitted successfully, showing success message');

      // Show success message
      Flushbar(
        message: response.message.isNotEmpty
            ? response.message
            : 'Application submitted successfully!',
        backgroundColor: Colors.green.withOpacity(0.9),
        duration: const Duration(seconds: 2),
        margin: const EdgeInsets.all(12),
        borderRadius: BorderRadius.circular(8),
        flushbarPosition: FlushbarPosition.TOP,
        icon: const Icon(
          Icons.check_circle,
          color: Colors.white,
        ),
      ).show(context);

      // Navigate after the flushbar duration (2.5 seconds to be safe)
      Timer(const Duration(milliseconds: 2500), () {
        if (context.mounted) {
          print('🚀 Timer completed, navigating to home');
          try {
            // Pop all routes back to the base route (which should be home)
            Navigator.of(context).popUntil((route) => route.isFirst);
            print('✅ Navigation to home completed');
          } catch (e) {
            print('❌ Navigation error: $e');
            // Fallback: just pop this page
            try {
              if (Navigator.of(context).canPop()) {
                Navigator.of(context).pop();
              }
            } catch (e2) {
              print('❌ Fallback navigation also failed: $e2');
            }
          }
        }
      });
    } else {
      Flushbar(
        message: response.message,
        backgroundColor: Colors.red.withOpacity(0.9),
        duration: const Duration(seconds: 4),
        margin: const EdgeInsets.all(12),
        borderRadius: BorderRadius.circular(8),
        flushbarPosition: FlushbarPosition.TOP,
        icon: const Icon(
          Icons.error,
          color: Colors.white,
        ),
      ).show(context);
    }
  }



  @override
  void initState() {
    super.initState();
    controller = MultiStepApplicationController(widget.application);
    controller.initialize();
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authProvider);
    const primaryGreen = Color(0xFF4CAF50);

    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        title: Text(widget.application.name),
        backgroundColor: primaryGreen,
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
                      flex: 1,
                      child: OutlinedButton(
                        onPressed: controller.isLoading
                            ? null
                            : () {
                                setState(() {
                                  controller.previousStep();
                                });
                              },
                        style: OutlinedButton.styleFrom(
                          foregroundColor: primaryGreen,
                          side: BorderSide(color: primaryGreen),
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
                    flex: 2,
                    child: ElevatedButton(
                      onPressed: controller.isLoading
                          ? null
                          : (controller.currentStep == controller.totalSteps - 1
                              ? () => _submitApplication(context, authState)
                              : () {
                                  setState(() {
                                    controller.nextStep();
                                  });
                                }),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: primaryGreen,
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 12),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        elevation: 2,
                      ),
                      child: controller.isLoading
                          ? const SizedBox(
                              height: 20,
                              width: 20,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                              ),
                            )
                          : Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Flexible(
                                  child: Text(
                                    controller.currentStep == controller.totalSteps - 1
                                        ? 'Submit'
                                        : 'Next',
                                    style: const TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.w600,
                                    ),
                                    textAlign: TextAlign.center,
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
