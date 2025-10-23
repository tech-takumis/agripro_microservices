import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/presentation/controllers/multi_step_registration_controller.dart';
import 'package:mobile/presentation/widgets/common/step_indicator.dart';
import 'package:mobile/presentation/widgets/common/custom_button.dart';
import 'package:mobile/presentation/widgets/registration/registration_step_one.dart';
import 'package:mobile/presentation/widgets/registration/registration_step_two.dart';
import 'package:mobile/presentation/widgets/registration/registration_step_three.dart';

/// Multi-step registration page with improved UX
class MultiStepRegisterPage extends ConsumerStatefulWidget {
  const MultiStepRegisterPage({super.key});

  @override
  ConsumerState<MultiStepRegisterPage> createState() => _MultiStepRegisterPageState();
}

class _MultiStepRegisterPageState extends ConsumerState<MultiStepRegisterPage> {
  late PageController _pageController;

  // References to step widgets for accessing their data
  final GlobalKey<RegistrationStepTwoState> _step2Key = GlobalKey();
  final GlobalKey<RegistrationStepThreeState> _step3Key = GlobalKey();

  @override
  void initState() {
    super.initState();
    _pageController = PageController(keepPage: true);
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  void _nextStep(MultiStepRegistrationController controller) {
    // Validate current step before proceeding
    final currentStep = controller.currentStep;
    final isValid = () {
      if (currentStep == 1) {
        return controller.step1FormKey.currentState?.validate() ?? false;
      } else if (currentStep == 2) {
        return controller.step2FormKey.currentState?.validate() ?? false;
      } else if (currentStep == 3) {
        return controller.step3FormKey.currentState?.validate() ?? false;
      }
      return false;
    }();

    if (!isValid) {
      // Do not proceed if the form is invalid
      return;
    }

    // Update data from current step before moving
    if (controller.currentStep == 2) {
      final step2State = _step2Key.currentState;
      if (step2State != null) {
        controller.updateGeographicData(
          region: step2State.selectedRegion,
          province: step2State.selectedProvince,
          city: step2State.selectedCity,
        );
      }
    } else if (controller.currentStep == 3) {
      final step3State = _step3Key.currentState;
      if (step3State != null) {
        controller.updateFarmData(
          tenureStatus: step3State.selectedTenureStatus,
          farmType: step3State.selectedFarmType,
        );
      }
    }

    controller.nextStep(context);
    if (controller.currentStep <= controller.totalSteps) {
      _pageController.nextPage(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  void _previousStep(MultiStepRegistrationController controller) {
    controller.previousStep();
    _pageController.previousPage(
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  void _submitRegistration(MultiStepRegistrationController controller) async {
    // Update data from step 3 before submitting
    final step3State = _step3Key.currentState;
    if (step3State != null) {
      controller.updateFarmData(
        tenureStatus: step3State.selectedTenureStatus,
        farmType: step3State.selectedFarmType,
      );
    }

    await controller.submitRegistration(context);
  }

  @override
  Widget build(BuildContext context) {
    final controller = ref.watch(multiStepRegistrationProvider);

    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        title: const Text('Create Account'),
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: controller.currentStep > 1
              ? () => _previousStep(controller)
              : () {
                  if (Navigator.of(context).canPop()) {
                    Navigator.of(context).pop();
                  } else {
                    GoRouter.of(context).go('/login');
                  }
                },
        ),
      ),
      body: SafeArea(
        child: Column(
          children: [
            // Header with logo and title
            Padding(
              padding: const EdgeInsets.all(24.0),
              child: Column(
                children: [
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
                  StepIndicator(
                    currentStep: controller.currentStep,
                    totalSteps: controller.totalSteps,
                    stepTitles: controller.stepTitles,
                  ),
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
                      formKey: controller.step1FormKey,
                      rsbsaNumberController: controller.rsbsaNumber,
                      passwordController: controller.passwordController,
                      dateOfBirthController: controller.dateOfBirthController,
                      genderController: controller.genderController,
                      civilStatusController: controller.civilStatusController,
                      firstNameController: controller.firstNameController,
                      lastNameController: controller.lastNameController,
                      middleNameController: controller.middleNameController,
                      emailController: controller.emailController,
                      phoneNumberController: controller.phoneNumberController,
                    ),
                  ),

                  // Step 2: Geographic Information
                  SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 24.0),
                    child: RegistrationStepTwo(
                      key: _step2Key,
                      formKey: controller.step2FormKey,
                      zipCodeController: controller.zipCodeController,
                      streetController: controller.streetController,
                      barangayController: controller.barangayController,
                    ),
                  ),

                  // Step 3: Farm Information
                  SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 24.0),
                    child: RegistrationStepThree(
                      key: _step3Key,
                      formKey: controller.step3FormKey,
                      farmLocationController: controller.farmLocationController,
                      farmSizeController: controller.farmSizeController,
                      primaryCropController: controller.primaryCropController,
                    ),
                  ),
                ],
              ),
            ),

            // Success/Error Messages
            if (controller.successMessage.isNotEmpty)
              Container(
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
                    if (controller.registrationResult?.username != null) ...[
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
                              controller.registrationResult!.username!,
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
              ),

            if (controller.errorMessage.isNotEmpty)
              Container(
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
                        controller.errorMessage,
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
              ),

            // Navigation Buttons
            Padding(
              padding: const EdgeInsets.all(24.0),
              child: Row(
                children: [
                  // Previous Button
                  if (controller.canGoPrevious)
                    Expanded(
                      child: CustomButton(
                        onPressed: () => _previousStep(controller),
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

                  if (controller.canGoPrevious) const SizedBox(width: 16),

                  // Next/Submit Button
                  Expanded(
                    flex: controller.canGoPrevious ? 1 : 2,
                    child: CustomButton(
                      onPressed: controller.isLoading
                          ? null
                          : controller.isLastStep
                          ? () => _submitRegistration(controller)
                          : () => _nextStep(controller),
                      isLoading: controller.isLoading,
                      child: Text(
                        controller.isLastStep ? 'Create Account' : 'Next',
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),

            // Success Message Actions
            if (controller.registrationResult?.success == true)
              Column(
                children: [
                  CustomButton(
                    onPressed: () => context.go('/login'),
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
                    onPressed: controller.resetForm,
                    child: const Text('Register Another Account'),
                  ),
                ],
              ),

            // Back to Login Link
            if (controller.registrationResult?.success != true)
              Padding(
                padding: const EdgeInsets.only(bottom: 24.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      'Already have an account? ',
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                    InkWell(
                      onTap: () {
                        if (Navigator.of(context).canPop()) {
                          Navigator.of(context).pop();
                        } else {
                          GoRouter.of(context).go('/login');
                        }
                      },
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
              ),
          ],
        ),
      ),
    );
  }
}
