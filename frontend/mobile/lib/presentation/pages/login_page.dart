import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../controllers/auth_controller.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/custom_button.dart';
import '../widgets/credentials_modal.dart';
import '../../data/services/storage_service.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _rememberMe = false.obs;
  final _obscurePassword = true.obs;

  final AuthController _authController = Get.find<AuthController>();

  @override
  void initState() {
    super.initState();
    _loadRememberMeStatus();
  }

  void _loadRememberMeStatus() {
    _rememberMe.value = StorageService.to.getRememberMe();
  }

  void _showCredentialsModal() {
    final credentials = StorageService.to.getSavedCredentials();
    if (credentials.isEmpty) {
      Get.snackbar(
        'No Saved Accounts',
        'Enable "Remember me" when logging in to save your credentials',
        snackPosition: SnackPosition.BOTTOM,
      );
      return;
    }

    Get.bottomSheet(
      CredentialsModal(
        onCredentialSelected: (username, password) {
          _usernameController.text = username;
          _passwordController.text = password;
          _rememberMe.value = true;
        },
      ),
      isScrollControlled: true,
    );
  }

  void _login() {
    if (!_formKey.currentState!.validate()) return;

    _authController.login(
      _usernameController.text.trim(),
      _passwordController.text,
      _rememberMe.value,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[50],
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const SizedBox(height: 60),

                // Logo or App Title
                Icon(
                  Icons.lock_outline,
                  size: 80,
                  color: Theme.of(context).primaryColor,
                ),
                const SizedBox(height: 24),

                Text(
                  'Welcome Back',
                  style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: Colors.grey[800],
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),

                Text(
                  'Sign in to your account',
                  style: Theme.of(
                    context,
                  ).textTheme.bodyLarge?.copyWith(color: Colors.grey[600]),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 48),

                // Username Field with saved accounts button
                CustomTextField(
                  controller: _usernameController,
                  label: 'Username',
                  prefixIcon: Icons.person_outline,
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.arrow_drop_down),
                    onPressed: _showCredentialsModal,
                    tooltip: 'Show saved accounts',
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'Please enter your username';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Password Field with saved accounts button
                Obx(
                  () => CustomTextField(
                    controller: _passwordController,
                    label: 'Password',
                    prefixIcon: Icons.lock_outline,
                    obscureText: _obscurePassword.value,
                    suffixIcon: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.arrow_drop_down),
                          onPressed: _showCredentialsModal,
                          tooltip: 'Show saved accounts',
                        ),
                        IconButton(
                          icon: Icon(
                            _obscurePassword.value
                                ? Icons.visibility
                                : Icons.visibility_off,
                          ),
                          onPressed: () => _obscurePassword.toggle(),
                        ),
                      ],
                    ),
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Please enter your password';
                      }
                      return null;
                    },
                  ),
                ),
                const SizedBox(height: 16),

                // Remember Me Checkbox
                Obx(
                  () => Row(
                    children: [
                      Checkbox(
                        value: _rememberMe.value,
                        onChanged:
                            (value) => _rememberMe.value = value ?? false,
                      ),
                      const Text('Remember me'),
                      const Spacer(),
                      if (StorageService.to.getSavedCredentials().isNotEmpty)
                        TextButton.icon(
                          onPressed: _showCredentialsModal,
                          icon: const Icon(Icons.account_circle, size: 16),
                          label: Text(
                            '${StorageService.to.getSavedCredentials().length} saved',
                            style: const TextStyle(fontSize: 12),
                          ),
                        ),
                    ],
                  ),
                ),
                const SizedBox(height: 24),

                // Error Message
                Obx(() {
                  if (_authController.errorMessage.isEmpty) {
                    return const SizedBox.shrink();
                  }

                  return Container(
                    padding: const EdgeInsets.all(12),
                    margin: const EdgeInsets.only(bottom: 16),
                    decoration: BoxDecoration(
                      color: Colors.red[50],
                      border: Border.all(color: Colors.red[300]!),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Row(
                      children: [
                        Icon(
                          Icons.error_outline,
                          color: Colors.red[700],
                          size: 20,
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: Text(
                            _authController.errorMessage,
                            style: TextStyle(color: Colors.red[700]),
                          ),
                        ),
                        IconButton(
                          onPressed: _authController.clearError,
                          icon: Icon(
                            Icons.close,
                            color: Colors.red[700],
                            size: 16,
                          ),
                        ),
                      ],
                    ),
                  );
                }),

                // Login Button
                Obx(
                  () => CustomButton(
                    onPressed: _authController.isLoading ? null : _login,
                    isLoading: _authController.isLoading,
                    child: const Text(
                      'Sign In',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 24),

                // Register Link
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      "Don't have an account? ",
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                    GestureDetector(
                      onTap: () => Get.toNamed('/register'),
                      child: Text(
                        'Register here',
                        style: TextStyle(
                          color: Theme.of(context).primaryColor,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }
}
