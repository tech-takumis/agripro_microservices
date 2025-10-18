import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/presentation/pages/home_page.dart';

import '../../../presentation/controllers/auth_controller.dart';
import '../../../presentation/pages/login_page.dart';
// Import other pages as needed

final goRouterProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authProvider);
  return GoRouter(
    initialLocation: authState.isLoggedIn ? '/home' : '/login',
    redirect: (context, state) {
      if (authState.isLoading) return null; // Don't redirect while loading
      if (!authState.isLoggedIn && state.uri.toString() != '/login') {
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
      // Add other routes here
    ],
  );
});
