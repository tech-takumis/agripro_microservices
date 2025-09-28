import 'package:get/get.dart';
import '../../data/models/application_data.dart';
import '../../data/services/api_service.dart';

class ApplicationController extends GetxController {
  final _applications = <ApplicationContent>[].obs;
  final _isLoading = true.obs;
  final _errorMessage = ''.obs;
  final _isRetrying = false.obs;

  List<ApplicationContent> get applications => _applications;
  bool get isLoading => _isLoading.value;
  bool get isRetrying => _isRetrying.value;
  String get errorMessage => _errorMessage.value;

  @override
  void onInit() {
    super.onInit();
    fetchApplications();
  }

  Future<void> fetchApplications() async {
    try {
      _isLoading.value = true;
      _errorMessage.value = '';

      print('🔄 Starting to fetch applications...');
      final response = await ApiService.to.fetchApplications();

      _applications.value = response.content;
      print('✅ Successfully loaded ${response.content.length} applications');
    } catch (e, stack) {
      print('❌ Error fetching applications: $e');
      print('❌ Stack: $stack');
      _errorMessage.value = e.toString().replaceFirst('Exception: ', '');
    } finally {
      _isLoading.value = false;
    }
  }

  Future<void> retryFetchApplications() async {
    try {
      _isRetrying.value = true;
      _errorMessage.value = '';

      final response = await ApiService.to.fetchApplications();

      _applications.value = response.content;
      print(
        '✅ Successfully loaded ${response.content.length} applications on retry',
      );
    } catch (e) {
      print('❌ Error retrying applications fetch: $e');
      _errorMessage.value = e.toString().replaceFirst('Exception: ', '');
    } finally {
      _isRetrying.value = false;
    }
  }

  void clearError() {
    _errorMessage.value = '';
  }
}
