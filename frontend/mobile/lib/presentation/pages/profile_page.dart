import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../controllers/auth_controller.dart';
import 'package:mobile/data/services/websocket.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/data/models/user_credentials.dart';
import 'application_tracker_page.dart';
import '../../injection_container.dart';

class ProfilePage extends ConsumerStatefulWidget {
  const ProfilePage({super.key});

  @override
  ConsumerState<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends ConsumerState<ProfilePage> {
  UserCredentials? _userCredentials;

  @override
  void initState() {
    super.initState();
    // Get user credentials from storage using getIt
    _userCredentials = getIt<StorageService>().getUserCredentials();
  }

  void _handleLogout() {
    // Disconnect WebSocket before logging out
    final webSocketService = getIt<WebSocketService>();
    if (webSocketService.isConnected) {
      print('ProfilePage: Disconnecting WebSocket before logout');
      webSocketService.disconnect();
    }
    // Proceed with normal logout using Riverpod AuthNotifier
    ref.read(authProvider.notifier).logout();
  }

  @override
  Widget build(BuildContext context) {

    final userName = _userCredentials != null
        ? "${_userCredentials?.user?.firstName ?? ''} ${_userCredentials?.user?.lastName ?? ''}".trim()
        : '';
    final userEmail = _userCredentials?.user?.email ?? '';

    return SafeArea(
      child: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 20),
            CircleAvatar(
              radius: 50,
              backgroundColor: Colors.green[100],
              child: const Icon(Icons.person, size: 60, color: Colors.green),
            ),
            const SizedBox(height: 16),
            Text(
              userName.isNotEmpty ? userName : 'No Name',
              style: const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.black87,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              userEmail.isNotEmpty ? userEmail : 'No Email',
              style: const TextStyle(fontSize: 14, color: Colors.grey),
            ),
            const SizedBox(height: 30),
            _buildSectionHeader('My Applications'),
            ListTile(
              leading: const Icon(Icons.assignment_outlined, color: Colors.green),
              title: const Text('Track My Applications'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: () {
                Navigator.of(context).push(
                  MaterialPageRoute(builder: (context) => const ApplicationTrackerPage()),
                );
              },
            ),
            const SizedBox(height: 20),
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
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: _handleLogout,
                icon: const Icon(Icons.logout),
                label: const Text(
                  'Logout',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color.fromARGB(209, 242, 16, 16),
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
