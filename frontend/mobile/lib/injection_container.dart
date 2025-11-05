import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:mobile/data/services/application_api_service.dart';
import 'package:mobile/data/services/auth_api_service.dart';
import 'package:mobile/data/services/document_service.dart';
import 'package:mobile/data/services/post_api_service.dart';
import 'package:mobile/data/services/location_service.dart';
import 'package:mobile/data/services/psgc_service.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/presentation/controllers/application_controller.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';
import 'data/services/message_service.dart';
import 'data/services/websocket.dart';
import 'data/services/notification_api.dart';
import 'data/services/notification_service.dart';

final getIt = GetIt.instance;

Future<void> setupDependencies() async {
  // Core services
  getIt.registerSingleton<Dio>(Dio());

  // Register AuthState for use in providers and controllers
  getIt.registerSingleton<AuthState>(AuthState());

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

  // Register PostApiService with authenticated Dio instance
  getIt.registerSingleton<PostApiService>(
    PostApiService(authDio, baseUrl: 'http://localhost:9001/api/v1'),
  );

  final psgcDio = Dio();
  getIt.registerSingleton<PSGCService>(
    PSGCService(psgcDio),
  );

  // Register NotificationApiService with authenticated Dio instance
  getIt.registerSingleton<NotificationApiService>(
    NotificationApiService(authDio, baseUrl: 'http://localhost:9001/api/v1'),
  );

  // Other services
  getIt.registerSingleton<LocationService>(LocationService());
  getIt.registerSingleton<WebSocketService>(WebSocketService());
  getIt.registerLazySingleton<DocumentService>(() => DocumentService());
  getIt.registerLazySingleton<MessageService>(() => MessageService());
  getIt.registerLazySingleton<NotificationService>(() => NotificationService());

  // Controllers
  getIt.registerSingleton<ApplicationController>(
    ApplicationController(),
  );

}