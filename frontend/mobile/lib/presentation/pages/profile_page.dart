import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../controllers/auth_controller.dart';
import 'application_tracker_page.dart'; // 👈 new page you'll create

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  final AuthController authController = Get.find<AuthController>();

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            // --- Profile Avatar & Info ---
            const SizedBox(height: 20),
            CircleAvatar(
              radius: 50,
              backgroundColor: Colors.green[100],
              child: const Icon(Icons.person, size: 60, color: Colors.green),
            ),
            const SizedBox(height: 16),

            // --- User Info ---
            Obx(() => Text(
                  authController.userName.isNotEmpty
                      ? authController.userName
                      : 'Albert John Agbo',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: Colors.black87,
                  ),
                )),
            const SizedBox(height: 4),
            Obx(() => Text(
                  authController.userEmail.isNotEmpty
                      ? authController.userEmail
                      : 'albertjohn@gmail.com',
                  style: const TextStyle(fontSize: 14, color: Colors.grey),
                )),
            const SizedBox(height: 30),

            // --- 🧾 Applications Section ---
            _buildSectionHeader('My Applications'),
            ListTile(
              leading: const Icon(Icons.assignment_outlined, color: Colors.green),
              title: const Text('Track My Applications'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: () {
                // Navigate to the tracker page
                Get.to(() => const ApplicationTrackerPage());
              },
            ),

            const SizedBox(height: 20),

            // --- ⚙️ Account Settings ---
            _buildSectionHeader('Account Settings'),
            ListTile(
              leading: const Icon(Icons.person_outline, color: Colors.green),
              title: const Text('Edit Profile'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: () {},
            ),
            ListTile(
              leading: const Icon(Icons.lock_outline, color: Colors.green),
              title: const Text('Change Password'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: () {},
            ),

            const SizedBox(height: 12),
            _buildSectionHeader('Support & Info'),
            ListTile(
              leading: const Icon(Icons.help_outline, color: Colors.green),
              title: const Text('Help & Support'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: () {},
            ),
            ListTile(
              leading: const Icon(Icons.info_outline, color: Colors.green),
              title: const Text('About the App'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: () {},
            ),

            const SizedBox(height: 40),

            // --- 🚪 Logout Button ---
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: authController.logout,
                icon: const Icon(Icons.logout),
                label: const Text(
                  'Logout',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color.fromARGB(255, 237, 14, 14),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 14),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  elevation: 2,
                ),
              ),
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }

  // --- Helper: Section Header ---
  Widget _buildSectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 8),
      child: Align(
        alignment: Alignment.centerLeft,
        child: Text(
          title,
          style: const TextStyle(
            fontWeight: FontWeight.w600,
            color: Colors.black54,
            fontSize: 15,
          ),
        ),
      ),
    );
  }
}
