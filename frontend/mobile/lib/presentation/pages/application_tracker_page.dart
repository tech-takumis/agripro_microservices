import 'package:flutter/material.dart';

class ApplicationTrackerPage extends StatelessWidget {
  const ApplicationTrackerPage({super.key});

  @override
  Widget build(BuildContext context) {
    // ✅ Explicitly type the maps
    final List<Map<String, dynamic>> applications = [
      {
        'title': 'Crop Insurance Application',
        'submittedDate': 'Sept 30, 2025',
        'status': 'Under Review',
        'progress': 1,
      },
      {
        'title': 'Fertilizer Assistance Program',
        'submittedDate': 'Oct 1, 2025',
        'status': 'Approved',
        'progress': 2,
      },
    ];

    return Scaffold(
      appBar: AppBar(
        title: const Text('My Application Tracker'),
        backgroundColor: Colors.green,
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: applications.length,
        itemBuilder: (context, index) {
          final app = applications[index];

          // ✅ Safely cast each value to the correct type
          return _buildStatusCard(
            title: app['title'] as String,
            submittedDate: app['submittedDate'] as String,
            status: app['status'] as String,
            currentStep: app['progress'] as int,
          );
        },
      ),
    );
  }

  // --- Helper: Application Status Card ---
  Widget _buildStatusCard({
    required String title,
    required String submittedDate,
    required String status,
    required int currentStep,
  }) {
    final steps = ['Submitted', 'Under Review', 'Approved', 'Completed'];

    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 8,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title,
              style: const TextStyle(
                  fontSize: 16, fontWeight: FontWeight.bold)),
          const SizedBox(height: 4),
          Text('Submitted on $submittedDate',
              style: const TextStyle(fontSize: 13, color: Colors.grey)),
          const SizedBox(height: 8),
          Text('Status: $status',
              style: const TextStyle(
                  fontSize: 13,
                  color: Colors.green,
                  fontWeight: FontWeight.w600)),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: List.generate(steps.length, (index) {
              final isActive = index <= currentStep;
              return Column(
                children: [
                  CircleAvatar(
                    radius: 12,
                    backgroundColor:
                        isActive ? Colors.green : Colors.grey.shade300,
                    child: Icon(
                      isActive ? Icons.check : Icons.circle,
                      size: 14,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    steps[index],
                    style: TextStyle(
                      fontSize: 10,
                      color: isActive ? Colors.green : Colors.grey,
                      fontWeight:
                          isActive ? FontWeight.w600 : FontWeight.normal,
                    ),
                  ),
                ],
              );
            }),
          ),
        ],
      ),
    );
  }
}
