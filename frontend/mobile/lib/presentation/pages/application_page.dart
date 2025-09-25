import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../controllers/application_controller.dart';
import 'application_form_page.dart'; // Import the form page

class ApplicationPage extends StatelessWidget {
  const ApplicationPage({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = Get.find<ApplicationController>();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Applications'),
        backgroundColor: Theme.of(context).primaryColor,
        foregroundColor: Colors.white,
        actions: [
          Obx(
            () => IconButton(
              onPressed:
                  controller.isLoading || controller.isRetrying
                      ? null
                      : controller.fetchApplications,
              icon:
                  controller.isLoading || controller.isRetrying
                      ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(
                            Colors.white,
                          ),
                        ),
                      )
                      : const Icon(Icons.refresh),
              tooltip: 'Refresh',
            ),
          ),
        ],
      ),
      body: Obx(() {
        if (controller.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }
        if (controller.errorMessage.isNotEmpty) {
          return Center(child: Text(controller.errorMessage));
        }
        if (controller.applications.isEmpty) {
          return const Center(child: Text('No applications found.'));
        }
        return ListView.builder(
          itemCount: controller.applications.length,
          itemBuilder: (context, index) {
            final app = controller.applications[index];
            return Card(
              child: ListTile(
                title: Text(app.name),
                subtitle: Text(app.description),
                trailing: const Icon(Icons.arrow_forward_ios),
                onTap: () {
                  Get.to(() => ApplicationFormPage(application: app));
                },
              ),
            );
          },
        );
      }),
    );
  }
}
