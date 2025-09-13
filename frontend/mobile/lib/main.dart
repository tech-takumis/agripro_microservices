import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'data/services/storage_service.dart';
import 'data/services/api_service.dart';
import 'controllers/auth_controller.dart';
import 'controllers/application_controller.dart'; // Import new controller
import 'presentation/pages/login_page.dart';
import 'presentation/pages/register_page.dart';
import 'presentation/pages/home_page.dart';
import 'presentation/pages/application_page.dart'; // Import new page

// Remember to run `flutter pub get` and
//`flutter pub run build_runner build --delete-conflicting-outputs`
//after these changes to generate the Hive adapter and fetch new packages.

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize services
  await Get.putAsync(() => StorageService().init());
  Get.put(ApiService());
  Get.put(AuthController());
  Get.put(ApplicationController()); // Initialize ApplicationController

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
        GetPage(name: '/register', page: () => const RegisterPage()),
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
