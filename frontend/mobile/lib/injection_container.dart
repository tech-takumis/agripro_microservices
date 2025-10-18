import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:mobile/data/services/application_api_service.dart';
import 'package:mobile/data/services/auth_api_service.dart';
import 'package:mobile/data/services/location_service.dart';
import 'package:mobile/data/services/psgc_service.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/presentation/controllers/application_controller.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';
import 'package:mobile/presentation/pages/home_page.dart';
import 'package:mobile/presentation/pages/login_page.dart';
import 'package:mobile/presentation/pages/multi_step_register_page.dart';

import 'data/services/message_service.dart';
import 'data/services/websocket.dart';

final getIt = GetIt.instance;

Future<void> setupDependencies() async {
  // Core services
  getIt.registerSingleton<Dio>(Dio());

  // Register StorageService first (async) WITHOUT requiring AuthApiService to avoid
  // circular dependency. We'll attach AuthApiService later.
  getIt.registerSingletonAsync<StorageService>(() async {
    final storageService = StorageService();
    await storageService.init();
    return storageService;
  });

  // Wait for StorageService to be ready before creating services that depend on it
  final storage = await getIt.getAsync<StorageService>();

  // API services (AuthApiService depends on StorageService)
  final authDio = Dio();
  final authApi = AuthApiService(
    authDio,
    baseUrl: 'http://localhost:9001/api/v1',
    storageService: storage,
  );
  getIt.registerSingleton<AuthApiService>(authApi);

  // Attach AuthApiService to StorageService now that both are registered.
  await storage.attachAuthApiService(authApi);

  // Other API services
  final appDio = Dio();
  getIt.registerSingleton<ApplicationApiService>(
    ApplicationApiService(appDio, baseUrl: 'http://localhost:9001/api/v1'),
  );
  final psgcDio = Dio();
  getIt.registerSingleton<PSGCService>(
    PSGCService(psgcDio),
  );

  // Other services
  getIt.registerSingleton<LocationService>(LocationService());
  getIt.registerSingleton<WebSocketService>(WebSocketService());
  getIt.registerSingleton<MessageService>(MessageService());

  // Controllers
  getIt.registerSingleton<ApplicationController>(
    ApplicationController(),
  );

  // Register AuthState for use in providers and controllers
  getIt.registerSingleton<AuthState>(AuthState());

  // Navigation
  getIt.registerSingleton<GoRouter>(
    GoRouter(
      initialLocation: '/login',
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
          path: '/register',
          builder: (context, state) => const MultiStepRegisterPage(),
        ),
      ],
    ),
  );
}