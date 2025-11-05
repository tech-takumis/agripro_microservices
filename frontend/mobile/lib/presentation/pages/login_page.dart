import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:geolocator/geolocator.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/data/services/storage_service.dart';
import '../../data/services/location_service.dart';
import '../../injection_container.dart';
import '../controllers/auth_controller.dart';
import '../widgets/common/custom_button.dart';
import '../widgets/common/custom_text_field.dart';
import '../widgets/credentials_modal.dart';

final authControllerProvider = authProvider;

class LoginPage extends ConsumerStatefulWidget {
  const LoginPage({super.key});

  @override
  ConsumerState<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends ConsumerState<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _rememberMe = ValueNotifier<bool>(false);
  final _obscurePassword = ValueNotifier<bool>(true);

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final authState = ref.read(authControllerProvider);
      if (authState.errorMessage == 'Location services required') {
        _showLocationPromptDialog();
      }
    });
  }

  void _showCredentialsModal() {
    final credentials = getIt<StorageService>().getUserCredentialsList();
    if (credentials.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('No saved accounts. Enable "Remember me" when logging in.'),
          duration: Duration(seconds: 3),
        ),
      );
      return;
    }

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => CredentialsModal(
        onCredentialSelected: (username, password) {
          _usernameController.text = username;
          _passwordController.text = password;
          _rememberMe.value = true;
        },
      ),
    );
  }

  void _login() {
    if (!_formKey.currentState!.validate()) return;
    ref.read(authControllerProvider.notifier).login(
      _usernameController.text.trim(),
      _passwordController.text,
      _rememberMe.value,
    );
  }

  void _showLocationPromptDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Text('Location Services Required'),
        content: const Text(
          'To automatically capture GPS coordinates, please enable location services and grant permissions in your device settings.',
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              ref.read(authControllerProvider.notifier).clearError();
            },
            child: const Text('Later'),
          ),
          TextButton(
            onPressed: () async {
              Navigator.of(context).pop();
              final locationService = getIt<LocationService>();
              bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
              if (!serviceEnabled) {
                await locationService.openLocationSettings();
              } else {
                await locationService.openAppSettings();
              }
              ref.read(authControllerProvider.notifier).clearError();
            },
            child: const Text('Open Settings'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    ref.listen<AuthState>(authControllerProvider, (previous, next) {
      if (previous?.isLoggedIn == false && next.isLoggedIn == true) {
        if (mounted) context.go('/home');
      }
      if (previous?.isLoggedIn == true && next.isLoggedIn == false) {
        if (mounted) context.go('/login');
      }
    });

    final authState = ref.watch(authControllerProvider);

    return Scaffold(
      backgroundColor: Colors.grey[100],
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 40),
          child: Container(
            constraints: const BoxConstraints(maxWidth: 420),
            padding: const EdgeInsets.all(32),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 10,
                  offset: const Offset(0, 4),
                ),
              ],
            ),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  const Icon(
                    Icons.lock_outline,
                    size: 72,
                    color: Colors.green,
                  ),
                  const SizedBox(height: 20),
                  Text(
                    'Welcome Back!',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.w700,
                          color: Colors.grey[800],
                        ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    'Sign in to continue',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: Colors.grey[600],
                        ),
                  ),
                  const SizedBox(height: 36),

                  // Username
                  CustomTextField(
                    controller: _usernameController,
                    label: 'Username',
                    prefixIcon: Icons.person_outline,
                    suffixIcon: IconButton(
                      icon: const Icon(Icons.arrow_drop_down),
                      onPressed: _showCredentialsModal,
                    ),
                    validator: (value) =>
                        value == null || value.isEmpty ? 'Enter your username' : null,
                  ),
                  const SizedBox(height: 16),

                  // Password
                  ValueListenableBuilder<bool>(
                    valueListenable: _obscurePassword,
                    builder: (context, obscure, _) => CustomTextField(
                      controller: _passwordController,
                      label: 'Password',
                      prefixIcon: Icons.lock_outline,
                      obscureText: obscure,
                      suffixIcon: IconButton(
                        icon: Icon(obscure ? Icons.visibility : Icons.visibility_off),
                        onPressed: () => _obscurePassword.value = !obscure,
                      ),
                      validator: (value) =>
                          value == null || value.isEmpty ? 'Enter your password' : null,
                    ),
                  ),
                  const SizedBox(height: 10),

                  // Remember Me
                  ValueListenableBuilder<bool>(
                    valueListenable: _rememberMe,
                    builder: (context, remember, _) => Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Row(
                          children: [
                            Checkbox(
                              value: remember,
                              onChanged: (v) => _rememberMe.value = v ?? false,
                            ),
                            const Text('Remember me'),
                          ],
                        ),
                        if (getIt<StorageService>().getUserCredentialsList().isNotEmpty)
                          TextButton.icon(
                            onPressed: _showCredentialsModal,
                            icon: const Icon(Icons.account_circle, size: 16),
                            label: Text(
                              '${getIt<StorageService>().getUserCredentialsList().length} saved',
                              style: const TextStyle(fontSize: 12),
                            ),
                          ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 16),

                  // Error message
                  if (authState.errorMessage.isNotEmpty &&
                      authState.errorMessage != 'Location services required')
                    Container(
                      padding: const EdgeInsets.all(12),
                      margin: const EdgeInsets.only(bottom: 16),
                      decoration: BoxDecoration(
                        color: Colors.red[50],
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: Colors.red[300]!),
                      ),
                      child: Row(
                        children: [
                          Icon(Icons.error_outline, color: Colors.red[700]),
                          const SizedBox(width: 8),
                          Expanded(
                            child: Text(
                              authState.errorMessage,
                              style: TextStyle(color: Colors.red[700]),
                            ),
                          ),
                          IconButton(
                            icon: Icon(Icons.close, size: 16, color: Colors.red[700]),
                            onPressed: () =>
                                ref.read(authControllerProvider.notifier).clearError(),
                          ),
                        ],
                      ),
                    ),

                  // Sign In Button
                  CustomButton(
                    onPressed: authState.isLoading ? null : _login,
                    isLoading: authState.isLoading,
                    child: const Text(
                      'Sign In',
                      style: TextStyle(fontWeight: FontWeight.w600, fontSize: 16),
                    ),
                  ),
                  const SizedBox(height: 28),

                  // Register
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        "Don't have an account?",
                        style: TextStyle(color: Colors.grey[600]),
                      ),
                      const SizedBox(width: 6),
                      GestureDetector(
                        onTap: () => context.go('/register'),
                        child: Text(
                          'Register here',
                          style: TextStyle(
                            color: Colors.green[700],
                            fontWeight: FontWeight.w700,
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
      ),
    );
  }

  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    _rememberMe.dispose();
    _obscurePassword.dispose();
    super.dispose();
  }
}
