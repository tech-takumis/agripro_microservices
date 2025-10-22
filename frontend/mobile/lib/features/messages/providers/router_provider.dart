import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/presentation/pages/home_page.dart';

import '../../../presentation/controllers/auth_controller.dart';
import '../../../presentation/pages/login_page.dart';
import  'package:mobile/presentation/pages/profile_page.dart';
import 'package:mobile/presentation/pages/multi_step_register_page.dart';

final goRouterProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authProvider);
  return GoRouter(
    initialLocation: authState.isLoggedIn ? '/home' : '/login',
    redirect: (context, state) {
      if (authState.isLoading) return null; // Don't redirect while loading
      // Allow unauthenticated users to access /login and /register
      final allowedUnauthenticated = ['/login', '/register'];
      if (!authState.isLoggedIn && !allowedUnauthenticated.contains(state.uri.toString())) {
        return '/login';
      }
      if (authState.isLoggedIn && state.uri.toString() == '/login') {
        return '/home';
      }
      return null;
    },
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: '/home',
        builder: (context, state) => const HomePage(),
      ),
      GoRoute(
        path: '/profile',
         builder: (context, state) => const ProfilePage()
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const MultiStepRegisterPage(),
      ),
    ],
  );
});
