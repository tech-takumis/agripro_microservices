import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile/presentation/controllers/multi_step_registration_controller.dart';
import 'package:mobile/presentation/widgets/common/step_indicator.dart';
import 'package:mobile/presentation/widgets/common/custom_button.dart';
import 'package:mobile/presentation/widgets/registration/registration_step_one.dart';
import 'package:mobile/presentation/widgets/registration/registration_step_two.dart';
import 'package:mobile/presentation/widgets/registration/registration_step_three.dart';

/// Multi-step registration page with improved UX
class MultiStepRegisterPage extends StatefulWidget {
  const MultiStepRegisterPage({super.key});

  @override
  State<MultiStepRegisterPage> createState() => _MultiStepRegisterPageState();
}

class _MultiStepRegisterPageState extends State<MultiStepRegisterPage> {
  late MultiStepRegistrationController _controller;
  late PageController _pageController;

  // References to step widgets for accessing their data
  final GlobalKey<RegistrationStepTwoState> _step2Key = GlobalKey();
  final GlobalKey<RegistrationStepThreeState> _step3Key = GlobalKey();

  @override
  void initState() {
    super.initState();
    _controller = Get.put(MultiStepRegistrationController());
    _pageController = PageController(keepPage: true);
  }

  @override
  void dispose() {
    _pageController.dispose();
    Get.delete<MultiStepRegistrationController>();
    super.dispose();
  }


  void _nextStep() {
    // Validate current step before proceeding
    final currentStep = _controller.currentStep;
    final isValid = () {
      if (currentStep == 1) {
        return _controller.step1FormKey.currentState?.validate() ?? false;
      } else if (currentStep == 2) {
        return _controller.step2FormKey.currentState?.validate() ?? false;
      } else if (currentStep == 3) {
        return _controller.step3FormKey.currentState?.validate() ?? false;
      }
      return false;
    }();

    if (!isValid) {
      // Do not proceed if the form is invalid
      return;
    }

    // Update data from current step before moving
    if (_controller.currentStep == 2) {
      final step2State = _step2Key.currentState;
      if (step2State != null) {
        _controller.updateGeographicData(
          region: step2State.selectedRegion,
          province: step2State.selectedProvince,
          city: step2State.selectedCity,
        );
      }
    } else if (_controller.currentStep == 3) {
      final step3State = _step3Key.currentState;
      if (step3State != null) {
        _controller.updateFarmData(
          tenureStatus: step3State.selectedTenureStatus,
          farmType: step3State.selectedFarmType,
        );
      }
    }

    _controller.nextStep();
    if (_controller.currentStep <= _controller.totalSteps) {
      _pageController.nextPage(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  void _previousStep() {
    _controller.previousStep();
    _pageController.previousPage(
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  void _submitRegistration() async {
    // Update data from step 3 before submitting
    final step3State = _step3Key.currentState;
    if (step3State != null) {
      _controller.updateFarmData(
        tenureStatus: step3State.selectedTenureStatus,
        farmType: step3State.selectedFarmType,
      );
    }

    await _controller.submitRegistration();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        title: const Text('Create Account'),
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: Obx(() => IconButton(
              icon: const Icon(Icons.arrow_back),
              onPressed: _controller.currentStep > 1 ? _previousStep : () => Get.back(),
            )),
      ),
      body: SafeArea(
        child: Column(
          children: [
            // Header with logo and title
            Padding(
              padding: const EdgeInsets.all(24.0),
              child: Column(
                children: [
                  // Icon(
                  //   Icons.person_add_outlined,
                  //   size: 60,
                  //   color: Theme.of(context).primaryColor,
                  // ),
                  // const SizedBox(height: 16),
                  Text(
                    'Create Your Account',
                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: Colors.grey[800],
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 24),

                  // Step Indicator
                  Obx(() => StepIndicator(
                    currentStep: _controller.currentStep,
                    totalSteps: _controller.totalSteps,
                    stepTitles: _controller.stepTitles,
                  )),
                ],
              ),
            ),

            // Step Content
            Expanded(
              child: PageView(
                controller: _pageController,
                physics: const NeverScrollableScrollPhysics(),
                children: [
                  // Step 1: Basic Information
                  SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 24.0),
                    child: RegistrationStepOne(
                      formKey: _controller.step1FormKey,
                      rsbsaNumberController: _controller.rsbsaNumber,
                      firstNameController: _controller.firstNameController,
                      lastNameController: _controller.lastNameController,
                      middleNameController: _controller.middleNameController,
                      emailController: _controller.emailController,
                      phoneNumberController: _controller.phoneNumberController,
                    ),
                  ),

                  // Step 2: Geographic Information
                  SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 24.0),
                    child: RegistrationStepTwo(
                      key: _step2Key,
                      formKey: _controller.step2FormKey,
                      zipCodeController: _controller.zipCodeController,
                    ),
                  ),

                  // Step 3: Farm Information
                  SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 24.0),
                    child: RegistrationStepThree(
                      key: _step3Key,
                      formKey: _controller.step3FormKey,
                      farmLocationController: _controller.farmLocationController,
                      farmSizeController: _controller.farmSizeController,
                      primaryCropController: _controller.primaryCropController,
                    ),
                  ),
                ],
              ),
            ),

            // Success/Error Messages
            Obx(() {
              if (_controller.successMessage.isNotEmpty) {
                return Container(
                  margin: const EdgeInsets.all(24),
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    color: Colors.green[50],
                    border: Border.all(color: Colors.green[300]!),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Column(
                    children: [
                      Row(
                        children: [
                          Icon(
                            Icons.check_circle_outline,
                            color: Colors.green[700],
                            size: 24,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Text(
                              'Account Created Successfully!',
                              style: TextStyle(
                                color: Colors.green[700],
                                fontWeight: FontWeight.w600,
                                fontSize: 16,
                              ),
                            ),
                          ),
                        ],
                      ),
                      if (_controller.registrationResult?.username != null) ...[
                        const SizedBox(height: 12),
                        Container(
                          width: double.infinity,
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(8),
                            border: Border.all(color: Colors.green[200]!),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Your Username:',
                                style: TextStyle(
                                  fontSize: 14,
                                  color: Colors.grey[600],
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                _controller.registrationResult!.username!,
                                style: const TextStyle(
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                  fontFamily: 'monospace',
                                ),
                              ),
                              const SizedBox(height: 12),
                              Text(
                                'Your login credentials have been sent to your registered email address.',
                                style: TextStyle(
                                  fontSize: 14,
                                  color: Colors.green[700],
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ],
                  ),
                );
              }

              if (_controller.errorMessage.isNotEmpty) {
                return Container(
                  margin: const EdgeInsets.all(24),
                  padding: const EdgeInsets.all(16),
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
                          _controller.errorMessage,
                          style: TextStyle(
                            color: Colors.red[700],
                            fontSize: 14,
                          ),
                        ),
                      ),
                      IconButton(
                        onPressed: _controller.clearMessages,
                        icon: Icon(
                          Icons.close,
                          color: Colors.red[700],
                          size: 16,
                        ),
                      ),
                    ],
                  ),
                );
              }

              return const SizedBox.shrink();
            }),

            // Navigation Buttons
            Padding(
              padding: const EdgeInsets.all(24.0),
              child: Obx(() {
                if (_controller.registrationResult?.success == true) {
                  return Column(
                    children: [
                      CustomButton(
                        onPressed: () => Get.offAllNamed('/login'),
                        backgroundColor: Colors.green,
                        child: const Text(
                          'Go to Login',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                      const SizedBox(height: 12),
                      TextButton(
                        onPressed: _controller.resetForm,
                        child: const Text('Register Another Account'),
                      ),
                    ],
                  );
                }

                return Row(
                  children: [
                    // Previous Button
                    if (_controller.canGoPrevious)
                      Expanded(
                        child: CustomButton(
                          onPressed: _previousStep,
                          backgroundColor: Colors.grey[600],
                          child: const Text(
                            'Previous',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                      ),

                    if (_controller.canGoPrevious) const SizedBox(width: 16),

                    // Next/Submit Button
                    Expanded(
                      flex: _controller.canGoPrevious ? 1 : 2,
                      child: CustomButton(
                        onPressed: _controller.isLoading
                            ? null
                            : _controller.isLastStep
                            ? _submitRegistration
                            : _nextStep,
                        isLoading: _controller.isLoading,
                        child: Text(
                          _controller.isLastStep ? 'Create Account' : 'Next',
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ),
                  ],
                );
              }),
            ),

            // Back to Login Link
            Obx(() {
              if (_controller.registrationResult?.success == true) {
                return const SizedBox.shrink();
              }

              return Padding(
                padding: const EdgeInsets.only(bottom: 24.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      'Already have an account? ',
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                    GestureDetector(
                      onTap: () => Get.back(),
                      child: Text(
                        'Sign in here',
                        style: TextStyle(
                          color: Theme.of(context).primaryColor,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              );
            }),
          ],
        ),
      ),
    );
  }
}
