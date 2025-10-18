import 'package:crystal_navigation_bar/crystal_navigation_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/services/message_service.dart';
import 'package:mobile/data/services/websocket.dart';
import 'package:mobile/injection_container.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';

import 'application_page.dart';
import 'contact_department_page.dart';
import 'profile_page.dart';

// Riverpod provider for AuthController (replace with your actual provider)
final authControllerProvider = Provider<AuthState>((ref) {
  // You may want to use getIt or your own logic here
  return getIt<AuthState>();
});

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<HomePage> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  int _currentIndex = 0;
  late PageController _pageController;
  bool _webSocketListenerSet = false;

  @override
  void initState() {
    super.initState();
    _pageController = PageController(initialPage: _currentIndex);
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  Future<void> _initWebSocketConnection(String token, String userId) async {
    final webSocketService = getIt<WebSocketService>();
    print('🔌 [HomePage] Connecting WebSocket for user: $userId');
    await webSocketService.connect();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authProvider);

    // Set up the listener only once
    if (!_webSocketListenerSet) {
      _webSocketListenerSet = true;
      ref.listen<AuthState>(authProvider, (previous, next) {
        if (next.token != null && next.userId != null) {
          _initWebSocketConnection(next.token!, next.userId!);
        }
      });
    }

    return Scaffold(
      extendBody: true,
      body: PageView(
        controller: _pageController,
        onPageChanged: (index) {
          setState(() => _currentIndex = index);
        },
        children: [
          _buildHomePage(),
          const ApplicationPage(),
          const ProfilePage(),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.green,
        onPressed: () async {
          final token = authState.token;
          final userId = authState.userId;
          if (token == null || userId == null) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Authentication required. Please log in again.')),
            );
            return;
          }
          await getIt<MessageService>().init(token: token, userId: userId);
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const ContactDepartmentPage(serviceType: 'Chat with Agriculturist'),
            ),
          );
        },
        child: const Icon(Icons.chat_bubble_outline, color: Colors.white),
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.only(bottom: 16.0, left: 16.0, right: 16.0),
        child: CrystalNavigationBar(
          currentIndex: _currentIndex,
          onTap: (index) {
            setState(() {
              _currentIndex = index;
              _pageController.jumpToPage(index);
            });
          },
          indicatorColor: Theme.of(context).primaryColor,
          unselectedItemColor: const Color.fromARGB(255, 24, 171, 46),
          selectedItemColor: Theme.of(context).primaryColor,
          backgroundColor: const Color.fromARGB(255, 255, 255, 255).withAlpha(240),
          items: [
            CrystalNavigationBarItem(icon: Icons.home_outlined),
            CrystalNavigationBarItem(icon: Icons.description_outlined),
            CrystalNavigationBarItem(icon: Icons.person_outline),
          ],
        ),
      ),
    );
  }

  Widget _buildHomePage() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: const [
          Icon(Icons.home, size: 100, color: Colors.green),
          SizedBox(height: 24),
          Text(
            'Welcome to the Dashboard!',
            style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 12),
          Text(
            'Manage your agricultural programs easily.',
            style: TextStyle(fontSize: 16, color: Colors.grey),
          ),
        ],
      ),
    );
  }
}
