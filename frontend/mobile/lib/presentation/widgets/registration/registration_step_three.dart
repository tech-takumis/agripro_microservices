import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mobile/presentation/widgets/common//custom_text_field.dart';
import 'package:mobile/presentation/widgets/common//custom_dropdown.dart';

/// Third step of registration: Farm Information
class RegistrationStepThree extends StatefulWidget {
  final GlobalKey<FormState> formKey;
  final TextEditingController farmLocationController;
  final TextEditingController farmSizeController;
  final TextEditingController primaryCropController;

  const RegistrationStepThree({
    super.key,
    required this.formKey,
    required this.farmLocationController,
    required this.farmSizeController,
    required this.primaryCropController,
  });

  @override
  State<RegistrationStepThree> createState() => RegistrationStepThreeState();
}

class RegistrationStepThreeState extends State<RegistrationStepThree> with AutomaticKeepAliveClientMixin {
  // Dropdown options
  final List<String> _tenureStatusOptions = [
    'Owner',
    'Tenant',
    'Lessee',
    'Caretaker',
    'Others',
  ];

  final List<String> _farmTypeOptions = [
    'Rice Farm',
    'Corn Farm',
    'Vegetable Farm',
    'Fruit Farm',
    'Livestock Farm',
    'Poultry Farm',
    'Fishpond',
    'Mixed Farm',
    'Others',
  ];

  String? _selectedTenureStatus;
  String? _selectedFarmType;

  // Getters for selected values (to be used by parent widget)
  String get selectedTenureStatus => _selectedTenureStatus ?? '';
  String get selectedFarmType => _selectedFarmType ?? '';

  @override
  Widget build(BuildContext context) {
    super.build(context); // Important for AutomaticKeepAliveClientMixin
    return Form(
      key: widget.formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Header
          Text(
            'Farm Information',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: Colors.grey[800],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Please provide details about your farm',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 32),

          // Farm Address
          CustomTextField(
            controller: widget.farmLocationController,
            label: 'Farm Address *',
            prefixIcon: Icons.agriculture_outlined,
            keyboardType: TextInputType.streetAddress,
            maxLines: 2,
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your farm address';
              }
              if (value.trim().length < 10) {
                return 'Please provide a more detailed farm address';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Tenure Status
          CustomDropdown<String>(
            label: 'Tenure Status *',
            value: _selectedTenureStatus,
            items: _tenureStatusOptions,
            displayText: (status) => status,
            hint: 'Select your tenure status',
            prefixIcon: Icons.business_outlined,
            onChanged: (status) {
              setState(() => _selectedTenureStatus = status);
            },
            validator: (value) {
              if (value == null) {
                return 'Please select your tenure status';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Farm Size
          CustomTextField(
            controller: widget.farmSizeController,
            label: 'Farm Size (in hectares) *',
            prefixIcon: Icons.straighten_outlined,
            keyboardType: const TextInputType.numberWithOptions(decimal: true),
            inputFormatters: [
              FilteringTextInputFormatter.allow(RegExp(r'^\d*\.?\d*')),
            ],
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your farm size';
              }
              final size = double.tryParse(value.trim());
              if (size == null || size <= 0) {
                return 'Please enter a valid farm size';
              }
              if (size > 1000) {
                return 'Farm size seems too large. Please verify.';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Farm Type
          CustomDropdown<String>(
            label: 'Farm Type *',
            value: _selectedFarmType,
            items: _farmTypeOptions,
            displayText: (type) => type,
            hint: 'Select your farm type',
            prefixIcon: Icons.eco_outlined,
            onChanged: (type) {
              setState(() => _selectedFarmType = type);
            },
            validator: (value) {
              if (value == null) {
                return 'Please select your farm type';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Primary Crop
          CustomTextField(
            controller: widget.primaryCropController,
            label: 'Primary Crop *',
            prefixIcon: Icons.grass_outlined,
            keyboardType: TextInputType.text,
            inputFormatters: [
              FilteringTextInputFormatter.allow(RegExp(r'[a-zA-Z\s]')),
            ],
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your primary crop';
              }
              if (value.trim().length < 2) {
                return 'Please enter a valid crop name';
              }
              return null;
            },
          ),

          const SizedBox(height: 32),

          // Information Card
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.green[50],
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: Colors.green[200]!),
            ),
            child: Row(
              children: [
                Icon(Icons.check_circle_outline, color: Colors.green[700]),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Almost Done!',
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          color: Colors.green[700],
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'Review your information and click "Create Account" to complete your registration.',
                        style: TextStyle(
                          fontSize: 13,
                          color: Colors.green[600],
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  @override
  bool get wantKeepAlive => true;
}
