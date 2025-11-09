import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mobile/injection_container.dart';
import 'package:mobile/presentation/controllers/application_controller.dart';
import 'package:mobile/presentation/widgets/application_widgets/application_card.dart';
import 'application_detail_page.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';

/// 🌿 Redesigned Application Page
/// Minimalist UI without AppBar background but with pull-to-refresh.
class ApplicationPage extends ConsumerWidget {
  const ApplicationPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final controller = getIt<ApplicationController>();
    final authState = ref.watch(authProvider);

    // Fetch applications on login
    if (authState.isLoggedIn && controller.isLoading.value && controller.applications.isEmpty) {
      controller.fetchApplications(authState);
    }

    ref.listen<AuthState>(authProvider, (prev, next) {
      if (next.isLoggedIn && prev?.isLoggedIn != true) {
        controller.fetchApplications(next);
      }
    });

    // Function to handle refresh
    Future<void> _handleRefresh() async {
      if (authState.isLoggedIn) {
        await controller.fetchApplications(authState);
      }
    }

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            // Header section (simple text, no AppBar)
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 12),
              child: Row(
                children: [
                  Text(
                    'Applications',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.w700,
                          color: Colors.black87,
                        ),
                  ),
                ],
              ),
            ),

            // Main content section with RefreshIndicator
            Expanded(
              child: RefreshIndicator(
                onRefresh: _handleRefresh,
                color: Theme.of(context).primaryColor,
                child: Obx(() {
                  if (controller.isLoading.value) {
                    return const Center(child: CircularProgressIndicator());
                  }

                  if (controller.errorMessage.value.isNotEmpty) {
                    return SingleChildScrollView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      child: SizedBox(
                        height: MediaQuery.of(context).size.height * 0.7,
                        child: Center(
                          child: Padding(
                            padding: const EdgeInsets.all(24),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Icon(Icons.error_outline, size: 64, color: Colors.red[300]),
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
                                  style: const TextStyle(color: Colors.black),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    );
                  }

                  if (controller.applications.isEmpty) {
                    return SingleChildScrollView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      child: SizedBox(
                        height: MediaQuery.of(context).size.height * 0.7,
                        child: Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.inbox_outlined, size: 64, color: Colors.grey[400]),
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
                                'Pull down to refresh',
                                style: TextStyle(color: Colors.grey[500]),
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  }

                  // ✅ Application List
                  return ListView.builder(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    physics: const AlwaysScrollableScrollPhysics(),
                    itemCount: controller.applications.length,
                    itemBuilder: (context, index) {
                      final application = controller.applications[index];
                      return Padding(
                        padding: const EdgeInsets.only(bottom: 12),
                        child: ApplicationCard(
                          application: application,
                          onTap: () {
                            Navigator.of(context).push(
                              MaterialPageRoute(
                                builder: (_) => ApplicationDetailPage(application: application),
                              ),
                            );
                          },
                        ),
                      );
                    },
                  );
                }),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
