import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'data/services/storage_service.dart';
import 'data/services/api_service.dart';
import 'presentation/controllers/auth_controller.dart';
import 'presentation/controllers/application_controller.dart';
import 'presentation/pages/login_page.dart';
import 'presentation/pages/home_page.dart';
import 'presentation/pages/application_page.dart';
import 'package:mobile/presentation/pages/multi_step_register_page.dart';

// Remember to run `flutter pub get` and
//`flutter pub run build_runner build --delete-conflicting-outputs`
//after these changes to generate the Hive adapter and fetch new packages.

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize services
  await Get.putAsync(() => StorageService().init());
  Get.put(ApiService());
  Get.put(AuthController());
  Get.put(ApplicationController());

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: 'Flutter Login App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
        inputDecorationTheme: InputDecorationTheme(
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 12,
          ),
        ),
      ),
      initialRoute: _getInitialRoute(),
      getPages: [
        GetPage(name: '/login', page: () => const LoginPage()),
        GetPage(name: '/register', page: () => const MultiStepRegisterPage()),
        GetPage(name: '/home', page: () => const HomePage()),
        GetPage(
          name: '/applications',
          page: () => const ApplicationPage(),
        ), // New route
      ],
      debugShowCheckedModeBanner: false,
    );
  }

  String _getInitialRoute() {
    final authController = Get.find<AuthController>();
    return authController.isLoggedIn ? '/home' : '/login';
  }
}
