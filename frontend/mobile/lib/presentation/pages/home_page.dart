import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:crystal_navigation_bar/crystal_navigation_bar.dart';
import '../controllers/auth_controller.dart';
import 'application_page.dart';
import 'package:mobile/presentation/pages/profile_page.dart';
import 'contact_department_page.dart';
import 'package:mobile/data/services/websocket.dart'; // <-- import WebSocketService
import 'package:mobile/data/services/storage_service.dart'; // <-- import StorageService for token

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final AuthController authController = Get.find<AuthController>();
  int _currentIndex = 0;
  late PageController _pageController;
  final WebSocketService _webSocketService = WebSocketService();

  @override
  void initState() {
    super.initState();
    _pageController = PageController(initialPage: _currentIndex);

    if (authController.isAuthenticated) {
      final accessToken = StorageService.to.getToken();
      // Use correct SockJS URL without /websocket
      const wsUrl = 'http://localhost:9040/ws';  // or use 10.0.2.2 for emulator
      _webSocketService.connect(
        url: wsUrl,
        accessToken: accessToken ?? '',
        onConnect: (frame) {
          print('WebSocket connected');
        },
        onError: (error) {
          print('WebSocket error: $error');
        },
      );
    }
  }


  @override
  void dispose() {
    _pageController.dispose();
    _webSocketService.disconnect();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBody: true,
      body: PageView(
        controller: _pageController,
        onPageChanged: (index) {
          setState(() => _currentIndex = index);
        },
        children: [
          // 🏠 Home Page
          _buildHomePage(),

          // 📄 Application Page
          const ApplicationPage(),

          // 👤 Profile Page
          const ProfilePage(),
        ],
      ),

      // 💬 Floating Chat Button
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.green,
        onPressed: () {
          // navigate to chatbot-style page
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const ContactDepartmentPage(),
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
          backgroundColor:
              const Color.fromARGB(255, 255, 255, 255).withAlpha(240),
          items: [
            CrystalNavigationBarItem(icon: Icons.home_outlined),
            CrystalNavigationBarItem(icon: Icons.description_outlined),
            CrystalNavigationBarItem(icon: Icons.person_outline),
          ],
        ),
      ),
    );
  }

  // 🏠 HOME PAGE
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
