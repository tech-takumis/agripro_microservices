import 'package:get/get.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../data/models/application_data.dart';
import '../../data/services/application_api_service.dart';
import '../../injection_container.dart';

// Add a provider reference to check authentication state
class ApplicationController extends GetxController {
  var applications = <ApplicationContent>[].obs;
  var isLoading = true.obs;
  var errorMessage = ''.obs;
  var isRetrying = false.obs;

  Future<void> fetchApplications(AuthState authState) async {
    if (!(authState.isLoggedIn && authState.token != null && authState.token!.isNotEmpty)) {
      print('User not logged in, skipping fetchApplications');
      return;
    }
    try {
      isLoading.value = true;
      errorMessage.value = '';

      print('🔄 Starting to fetch applications...');
      final response = await getIt<ApplicationApiService>().fetchApplications(authState);

      applications.value = response!.content;
      print('✅ Successfully loaded \\${response.content.length} applications');
    } catch (e, stack) {
      print('❌ Error fetching applications: \\${e}');
      print('❌ Stack: \\${stack}');
      errorMessage.value = e.toString().replaceFirst('Exception: ', '');
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> retryFetchApplications(AuthState authState) async {
    if (!(authState.isLoggedIn && authState.token != null && authState.token!.isNotEmpty)) {
      print('User not logged in, skipping retryFetchApplications');
      return;
    }
    try {
      isRetrying.value = true;
      errorMessage.value = '';

      final response = await getIt<ApplicationApiService>().fetchApplications(authState);

      applications.value = response!.content;
      print('✅ Successfully loaded \\${response.content.length} applications on retry');
    } catch (e) {
      print('❌ Error retrying applications fetch: \\${e}');
      errorMessage.value = e.toString().replaceFirst('Exception: ', '');
    } finally {
      isRetrying.value = false;
    }
  }

  void clearError() {
    errorMessage.value = '';
  }
}
