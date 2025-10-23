import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/presentation/widgets/common/custom_text_field.dart';

// Riverpod provider for password match
final passwordMatchProvider = StateProvider.autoDispose<bool>((ref) => true);

/// First step of registration: Basic Information
class RegistrationStepOne extends ConsumerStatefulWidget {
  final GlobalKey<FormState> formKey;
  final TextEditingController rsbsaNumberController;
  final TextEditingController passwordController;
  final TextEditingController dateOfBirthController;
  final TextEditingController genderController;
  final TextEditingController civilStatusController;
  final TextEditingController firstNameController;
  final TextEditingController lastNameController;
  final TextEditingController middleNameController;
  final TextEditingController emailController;
  final TextEditingController phoneNumberController;

  const RegistrationStepOne({
    super.key,
    required this.formKey,
    required this.rsbsaNumberController,
    required this.passwordController,
    required this.dateOfBirthController,
    required this.genderController,
    required this.civilStatusController,
    required this.firstNameController,
    required this.lastNameController,
    required this.middleNameController,
    required this.emailController,
    required this.phoneNumberController,
  });

  @override
  ConsumerState<RegistrationStepOne> createState() => _RegistrationStepOneState();
}

class _RegistrationStepOneState extends ConsumerState<RegistrationStepOne> {
  final TextEditingController _confirmPasswordController = TextEditingController();
  bool _obscurePassword = true;
  bool _obscureConfirmPassword = true;

  @override
  void initState() {
    super.initState();
    widget.passwordController.addListener(_checkPasswordMatch);
    _confirmPasswordController.addListener(_checkPasswordMatch);
  }

  void _checkPasswordMatch() {
    final match = widget.passwordController.text == _confirmPasswordController.text;
    ref.read(passwordMatchProvider.notifier).state = match;
  }

  @override
  void dispose() {
    widget.passwordController.removeListener(_checkPasswordMatch);
    _confirmPasswordController.removeListener(_checkPasswordMatch);
    _confirmPasswordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final passwordsMatch = ref.watch(passwordMatchProvider);

    return Form(
      key: widget.formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Header
          Text(
            'Basic Information',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: Colors.grey[800],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Please provide your basic personal information',
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 32),

          // Information Card
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.blue[50],
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: Colors.blue[200]!),
            ),
            child: Row(
              children: [
                Icon(Icons.info_outline, color: Colors.blue[700]),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'RSBSA Registration',
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          color: Colors.blue[700],
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'Your login credentials will be sent to your registered email address after successful registration.',
                        style: TextStyle(
                          fontSize: 13,
                          color: Colors.blue[600],
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),

          // RSBSA Reference Number
          CustomTextField(
            controller: widget.rsbsaNumberController,
            label: 'RSBSA Reference Number *',
            prefixIcon: Icons.badge_outlined,
            keyboardType: TextInputType.text,
            // inputFormatters: [
            //   // FilteringTextInputFormatter.allow(RegExp(r'[\d-]')),
            //   // LengthLimitingTextInputFormatter(15),
            // ],
            // onChanged: (value) {
            //   if (value.length >= 3 &&
            //       !value.contains('-') &&
            //       value.length <= 6) {
            //     final formatted = RegistrationController.formatRsbsaId(value);
            //     if (formatted != value) {
            //       referenceNumberController.value = TextEditingValue(
            //         text: formatted,
            //         selection: TextSelection.collapsed(offset: formatted.length),
            //       );
            //     }
            //   }
            // },
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter your RSBSA Reference Number';
              }
              // if (!RegistrationController.isValidRsbsaId(value)) {
              //   return 'Please enter a valid RSBSA Reference Number';
              // }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Password
          CustomTextField(
            controller: widget.passwordController,
            label: 'Password *',
            prefixIcon: Icons.lock_outline,
            keyboardType: TextInputType.visiblePassword,
            obscureText: _obscurePassword,
            suffixIcon: IconButton(
              icon: Icon(
                _obscurePassword ? Icons.visibility_off : Icons.visibility,
              ),
              onPressed: () {
                setState(() {
                  _obscurePassword = !_obscurePassword;
                });
              },
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter a password';
              }
              if (value.length < 6) {
                return 'Password must be at least 6 characters';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Confirm Password
          CustomTextField(
            controller: _confirmPasswordController,
            label: 'Confirm Password *',
            prefixIcon: Icons.lock_outline,
            keyboardType: TextInputType.visiblePassword,
            obscureText: _obscureConfirmPassword,
            suffixIcon: IconButton(
              icon: Icon(
                _obscureConfirmPassword ? Icons.visibility_off : Icons.visibility,
              ),
              onPressed: () {
                setState(() {
                  _obscureConfirmPassword = !_obscureConfirmPassword;
                });
              },
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please confirm your password';
              }
              if (value.length < 6) {
                return 'Password must be at least 6 characters';
              }
              if (!passwordsMatch) {
                return 'Passwords do not match';
              }
              return null;
            },
          ),
          if (_confirmPasswordController.text.isNotEmpty && !passwordsMatch)
            Padding(
              padding: const EdgeInsets.only(top: 4.0, left: 8.0),
              child: Text(
                'Passwords do not match',
                style: TextStyle(color: Colors.red[700], fontSize: 12),
              ),
            ),
          const SizedBox(height: 20),
          // Date of Birth
          GestureDetector(
            onTap: () async {
              final picked = await showDatePicker(
                context: context,
                initialDate: DateTime(2000, 1, 1),
                firstDate: DateTime(1900),
                lastDate: DateTime(DateTime.now().year - 18, 12, 31),
              );
              if (picked != null) {
                widget.dateOfBirthController.text = '${picked.year}-${picked.month.toString().padLeft(2, '0')}-${picked.day.toString().padLeft(2, '0')}' ;
              }
            },
            child: AbsorbPointer(
              child: CustomTextField(
                controller: widget.dateOfBirthController,
                label: 'Date of Birth *',
                prefixIcon: Icons.cake_outlined,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please select your date of birth';
                  }
                  // Simple date format check
                  final regex = RegExp(r'^\d{4}-\d{2}-\d{2}');
                  if (!regex.hasMatch(value)) {
                    return 'Enter date as YYYY-MM-DD';
                  }
                  return null;
                },
              ),
            ),
          ),
          const SizedBox(height: 20),
          // Gender Dropdown
          DropdownButtonFormField<String>(
            value: widget.genderController.text.isNotEmpty ? widget.genderController.text : null,
            decoration: const InputDecoration(
              labelText: 'Gender *',
              prefixIcon: Icon(Icons.wc),
              border: OutlineInputBorder(),
            ),
            items: ['Male', 'Female', 'Other']
                .map((gender) => DropdownMenuItem(
                      value: gender,
                      child: Text(gender),
                    ))
                .toList(),
            onChanged: (value) {
              widget.genderController.text = value ?? '';
            },
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please select your gender';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),
          // Civil Status Dropdown
          DropdownButtonFormField<String>(
            value: widget.civilStatusController.text.isNotEmpty ? widget.civilStatusController.text : null,
            decoration: const InputDecoration(
              labelText: 'Civil Status *',
              prefixIcon: Icon(Icons.family_restroom),
              border: OutlineInputBorder(),
            ),
            items: ['Single', 'Married', 'Widowed', 'Separated', 'Divorced']
                .map((status) => DropdownMenuItem(
                      value: status,
                      child: Text(status),
                    ))
                .toList(),
            onChanged: (value) {
              widget.civilStatusController.text = value ?? '';
            },
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please select your civil status';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // First Name
          CustomTextField(
            controller: widget.firstNameController,
            label: 'First Name *',
            prefixIcon: Icons.person_outline,
            keyboardType: TextInputType.name,
            inputFormatters: [
              FilteringTextInputFormatter.allow(RegExp(r'[a-zA-Z\s]')),
            ],
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your first name';
              }
              if (value.trim().length < 2) {
                return 'First name must be at least 2 characters';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Last Name
          CustomTextField(
            controller: widget.lastNameController,
            label: 'Last Name *',
            prefixIcon: Icons.person_outline,
            keyboardType: TextInputType.name,
            inputFormatters: [
              FilteringTextInputFormatter.allow(RegExp(r'[a-zA-Z\s]')),
            ],
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your last name';
              }
              if (value.trim().length < 2) {
                return 'Last name must be at least 2 characters';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Middle Name (Optional)
          CustomTextField(
            controller: widget.middleNameController,
            label: 'Middle Name (Optional)',
            prefixIcon: Icons.person_outline,
            keyboardType: TextInputType.name,
            inputFormatters: [
              FilteringTextInputFormatter.allow(RegExp(r'[a-zA-Z\s]')),
            ],
          ),
          const SizedBox(height: 20),

          // Email
          CustomTextField(
            controller: widget.emailController,
            label: 'Email Address *',
            prefixIcon: Icons.email_outlined,
            keyboardType: TextInputType.emailAddress,
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your email address';
              }
              final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
              if (!emailRegex.hasMatch(value.trim())) {
                return 'Please enter a valid email address';
              }
              return null;
            },
          ),
          const SizedBox(height: 20),

          // Phone Number
          CustomTextField(
            controller: widget.phoneNumberController,
            label: 'Phone Number *',
            prefixIcon: Icons.phone_outlined,
            keyboardType: TextInputType.phone,
            inputFormatters: [
              FilteringTextInputFormatter.allow(RegExp(r'[\d\s\-\+$$$$]')),
              LengthLimitingTextInputFormatter(15),
            ],
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter your phone number';
              }
              final cleanNumber = value.replaceAll(RegExp(r'[\s\-\+$$$$]'), '');
              if (cleanNumber.length < 10) {
                return 'Please enter a valid phone number';
              }
              return null;
            },
          ),
        ],
      ),
    );
  }
}
