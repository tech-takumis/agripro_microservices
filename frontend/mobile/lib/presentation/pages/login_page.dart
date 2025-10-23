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

// Use the correct provider from your AuthController file
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
    // Removed ref.listen from here
  }

  void _showCredentialsModal() {
    final credentials = getIt<StorageService>().getUserCredentialsList();
    if (credentials.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('No Saved Accounts. Enable "Remember me" when logging in.'),
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

    // FIX: Use the notifier to call login, not the state
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
          'To automatically capture GPS coordinates for your application forms, please enable location services and grant permissions for this app in your device settings.',
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
        if (mounted) {
          context.go('/home');
        }
      }
      if (previous?.isLoggedIn == true && next.isLoggedIn == false) {
        if (mounted) {
          context.go('/login');
        }
      }
    });

    final authState = ref.watch(authControllerProvider);

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
                  style: Theme.of(context).textTheme.bodyLarge?.copyWith(color: Colors.grey[600]),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 48),
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
                ValueListenableBuilder<bool>(
                  valueListenable: _obscurePassword,
                  builder: (context, obscurePassword, child) => CustomTextField(
                    controller: _passwordController,
                    label: 'Password',
                    prefixIcon: Icons.lock_outline,
                    obscureText: obscurePassword,
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
                            obscurePassword ? Icons.visibility : Icons.visibility_off,
                          ),
                          onPressed: () => _obscurePassword.value = !obscurePassword,
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
                ValueListenableBuilder<bool>(
                  valueListenable: _rememberMe,
                  builder: (context, rememberMe, child) => Row(
                    children: [
                      Checkbox(
                        value: rememberMe,
                        onChanged: (value) => _rememberMe.value = value ?? false,
                      ),
                      const Text('Remember me'),
                      const Spacer(),
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
                const SizedBox(height: 24),
                if (authState.errorMessage.isNotEmpty &&
                    authState.errorMessage != 'Location services required')
                  Container(
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
                            authState.errorMessage,
                            style: TextStyle(color: Colors.red[700]),
                          ),
                        ),
                        IconButton(
                          onPressed: () => ref.read(authControllerProvider.notifier).clearError(),
                          icon: Icon(
                            Icons.close,
                            color: Colors.red[700],
                            size: 16,
                          ),
                        ),
                      ],
                    ),
                  ),
                CustomButton(
                  onPressed: authState.isLoading ? null : _login,
                  isLoading: authState.isLoading,
                  child: const Text(
                    'Sign In',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      "Don't have an account? ",
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                    GestureDetector(
                      onTap: () => context.go('/register'),
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
    _rememberMe.dispose();
    _obscurePassword.dispose();
    super.dispose();
  }
}