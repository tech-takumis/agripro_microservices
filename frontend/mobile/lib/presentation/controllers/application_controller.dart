import 'package:get/get.dart';

import '../../data/models/application_data.dart';
import '../../data/services/application_api_service.dart';
import '../../injection_container.dart';

class ApplicationController extends GetxController {
  var applications = <ApplicationContent>[].obs;
  var isLoading = true.obs;
  var errorMessage = ''.obs;
  var isRetrying = false.obs;

  ApplicationController({bool autoFetch = true}) {
    if (autoFetch) fetchApplications();
  }

  Future<void> fetchApplications() async {
    try {
      isLoading.value = true;
      errorMessage.value = '';

      print('🔄 Starting to fetch applications...');
      final response = await getIt<ApplicationApiService>().fetchApplications();

      applications.value = response.content;
      print('✅ Successfully loaded \\${response.content.length} applications');
    } catch (e, stack) {
      print('❌ Error fetching applications: \\${e}');
      print('❌ Stack: \\${stack}');
      errorMessage.value = e.toString().replaceFirst('Exception: ', '');
    } finally {
      isLoading.value = false;
    }
  }

  Future<void> retryFetchApplications() async {
    try {
      isRetrying.value = true;
      errorMessage.value = '';

      final response = await getIt<ApplicationApiService>().fetchApplications();

      applications.value = response.content;
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
