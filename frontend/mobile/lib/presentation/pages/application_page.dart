import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile/injection_container.dart';
import 'package:mobile/presentation/controllers/application_controller.dart';
import 'package:mobile/presentation/widgets/application_widgets/application_card.dart';
import 'application_detail_page.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';

/// 🌿 Main Application Page
/// Displays all available application types in card format.
class ApplicationPage extends ConsumerWidget {
  const ApplicationPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final controller = getIt<ApplicationController>();
    final authState = ref.watch(authProvider);

    // Ensure applications are fetched if already logged in and still loading
    if (authState.isLoggedIn && controller.isLoading.value && controller.applications.isEmpty) {
      controller.fetchApplications(authState);
    }

    // Listen for login state changes and fetch applications after login
    ref.listen<AuthState>(authProvider, (prev, next) {
      if (next.isLoggedIn && prev?.isLoggedIn != true) {
        controller.fetchApplications(next);
      }
    });

    return Scaffold(
      backgroundColor: const Color.fromARGB(72, 248, 248, 248),
      appBar: AppBar(
        title: const Text('Applications'),
        backgroundColor: Colors.green[700],
        foregroundColor: Colors.white,
        actions: [
          Obx(
            () => IconButton(
              onPressed: controller.isLoading.value || controller.isRetrying.value
                  ? null
                  : () => controller.fetchApplications(authState),
              icon: controller.isLoading.value || controller.isRetrying.value
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                      ),
                    )
                  : const Icon(Icons.refresh),
              tooltip: 'Refresh',
            ),
          ),
        ],
      ),
      body: Obx(() {
        // 🌀 Loading State
        if (controller.isLoading.value) {
          return const Center(
            child: CircularProgressIndicator(),
          );
        }

        // ❌ Error State
        if (controller.errorMessage.value.isNotEmpty) {
          return Center(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.error_outline,
                    size: 64,
                    color: Colors.red[300],
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Error Loading Applications',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    controller.errorMessage.value,
                    textAlign: TextAlign.center,
                    style: TextStyle(color: Colors.grey[600]),
                  ),
                  const SizedBox(height: 24),
                  ElevatedButton.icon(
                    onPressed: () => controller.retryFetchApplications(authState),
                    icon: const Icon(Icons.refresh),
                    label: const Text('Try Again'),
                  ),
                ],
              ),
            ),
          );
        }

        // 📭 Empty State
        if (controller.applications.isEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.inbox_outlined,
                  size: 64,
                  color: Colors.grey[400],
                ),
                const SizedBox(height: 16),
                Text(
                  'No Applications Available',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: Colors.grey[600],
                      ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Check back later for new applications',
                  style: TextStyle(color: Colors.grey[500]),
                ),
              ],
            ),
          );
        }

        // ✅ Success State — Show List
        return ListView.builder(
          padding: const EdgeInsets.symmetric(vertical: 16),
          itemCount: controller.applications.length,
          itemBuilder: (context, index) {
            final application = controller.applications[index];
            return ApplicationCard(
              application: application,
              onTap: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (_) => ApplicationDetailPage(application: application),
                  ),
                );
              },
            );
          },
        );
      }),
    );
  }
}
