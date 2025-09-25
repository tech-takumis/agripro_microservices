import 'package:flutter/material.dart';

/// A widget that displays the current step in a multi-step process
class StepIndicator extends StatelessWidget {
  final int currentStep;
  final int totalSteps;
  final List<String> stepTitles;
  final Color? activeColor;
  final Color? inactiveColor;
  final Color? completedColor;

  const StepIndicator({
    super.key,
    required this.currentStep,
    required this.totalSteps,
    required this.stepTitles,
    this.activeColor,
    this.inactiveColor,
    this.completedColor,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final activeCol = activeColor ?? theme.primaryColor;
    final inactiveCol = inactiveColor ?? Colors.grey[300]!;
    final completedCol = completedColor ?? Colors.green;

    return Column(
      children: [
        // Step indicators
        Row(
          children: List.generate(totalSteps, (index) {
            final stepNumber = index + 1;
            final isActive = stepNumber == currentStep;
            final isCompleted = stepNumber < currentStep;

            return Expanded(
              child: Row(
                children: [
                  // Step circle
                  Container(
                    width: 32,
                    height: 32,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: isCompleted
                          ? completedCol
                          : isActive
                          ? activeCol
                          : inactiveCol,
                      border: Border.all(
                        color: isCompleted
                            ? completedCol
                            : isActive
                            ? activeCol
                            : inactiveCol,
                        width: 2,
                      ),
                    ),
                    child: Center(
                      child: isCompleted
                          ? Icon(
                        Icons.check,
                        color: Colors.white,
                        size: 18,
                      )
                          : Text(
                        '$stepNumber',
                        style: TextStyle(
                          color: isActive ? Colors.white : Colors.grey[600],
                          fontWeight: FontWeight.bold,
                          fontSize: 14,
                        ),
                      ),
                    ),
                  ),

                  // Connecting line (except for last step)
                  if (index < totalSteps - 1)
                    Expanded(
                      child: Container(
                        height: 2,
                        margin: const EdgeInsets.symmetric(horizontal: 8),
                        color: stepNumber < currentStep ? completedCol : inactiveCol,
                      ),
                    ),
                ],
              ),
            );
          }),
        ),

        const SizedBox(height: 12),

        // Step titles
        Row(
          children: List.generate(totalSteps, (index) {
            final stepNumber = index + 1;
            final isActive = stepNumber == currentStep;
            final isCompleted = stepNumber < currentStep;

            return Expanded(
              child: Text(
                stepTitles[index],
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: isActive ? FontWeight.w600 : FontWeight.normal,
                  color: isCompleted
                      ? completedCol
                      : isActive
                      ? activeCol
                      : Colors.grey[600],
                ),
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
              ),
            );
          }),
        ),
      ],
    );
  }
}
