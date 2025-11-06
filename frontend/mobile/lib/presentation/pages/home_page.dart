import 'package:cached_network_image/cached_network_image.dart';
import 'package:carousel_slider/carousel_slider.dart' as carousel;
import 'package:crystal_navigation_bar/crystal_navigation_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/post_models.dart';
import 'package:mobile/data/services/message_service.dart';
import 'package:mobile/injection_container.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';
import 'package:mobile/presentation/controllers/post_controller.dart';
import 'package:mobile/presentation/widgets/loading_indicator.dart';
import 'package:mobile/presentation/widgets/error_retry_widget.dart';
import 'package:timeago/timeago.dart' as timeago;
import 'package:mobile/features/messages/providers/notification_provider.dart';
import 'application_page.dart';
import 'contact_department_page.dart';
import 'notification_page.dart';
import 'profile_page.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<HomePage> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  int _currentIndex = 0;
  late PageController _pageController;

  // Add helper method to get the appropriate icon
  IconData _getNotificationIcon(int unreadCount) {
    return unreadCount > 0
        ? Icons.notifications_active_rounded
        : Icons.notifications_rounded;
  }

  @override
  void initState() {
    super.initState();
    _pageController = PageController(initialPage: _currentIndex);
    timeago.setLocaleMessages('en', timeago.EnMessages());
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(postControllerProvider.notifier).fetchPosts();
    });
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }
@override
Widget build(BuildContext context) {
  final authState = ref.watch(authProvider);
  final notificationState = ref.watch(notificationProvider);
  final unreadCount = notificationState.unreadCount;

  return Scaffold(
    backgroundColor: Colors.white,
    extendBody: true,
    appBar: AppBar(
      automaticallyImplyLeading: false,
      toolbarHeight: 0,
      backgroundColor: Colors.transparent,
      elevation: 0,
    ),
    body: PageView(
      controller: _pageController,
      onPageChanged: (index) {
        setState(() => _currentIndex = index);
      },
      children: [
        _buildHomePage(),
        const ApplicationPage(),
        const NotificationPage(),
        const ProfilePage(),
      ],
    ),
    floatingActionButton: FloatingActionButton(
      backgroundColor: const Color.fromARGB(255, 53, 129, 40), // replaced green with dark neutral
      onPressed: () async {
        final token = authState.token;
        final userId = authState.userId;
        if (token == null || userId == null) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Please log in again.')),
          );
          return;
        }
        await getIt<MessageService>().init(token: token, userId: userId);
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => const ContactDepartmentPage(
              serviceType: 'Chat with Agriculturist',
            ),
          ),
        );
      },
      child: const Icon(Icons.chat_rounded, color: Colors.white),
    ),
    bottomNavigationBar: Padding(
      padding: const EdgeInsets.only(bottom: 16.0, left: 16.0, right: 16.0),
      child: CrystalNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) {
          _pageController.jumpToPage(index);
        },
        items: [
          CrystalNavigationBarItem(
            icon: Icons.home_rounded,
          ),
          CrystalNavigationBarItem(
            icon: Icons.assignment_rounded,
          ),
          CrystalNavigationBarItem(
            icon: _getNotificationIcon(unreadCount),
          ),
          CrystalNavigationBarItem(
            icon: Icons.person_rounded,
          ),
        ],
        indicatorColor: const Color.fromARGB(255, 53, 129, 40), // neutral indicator
        unselectedItemColor: const Color.fromARGB(255, 53, 129, 40),
        selectedItemColor: const Color.fromARGB(255, 187, 186, 186),
        backgroundColor: Color.fromARGB(255, 223, 240, 223).withOpacity(0.95),
      ),
    ),
  );
}

  Widget _buildNotificationIconWithBadge(int count) {
    return Stack(
      clipBehavior: Clip.none,
      children: [
        const Icon(Icons.notifications_rounded),
        if (count > 0)
          Positioned(
            right: -6,
            top: -6,
            child: Container(
              padding: const EdgeInsets.all(4),
              decoration: const BoxDecoration(
                color: Colors.red,
                shape: BoxShape.circle,
              ),
              constraints: const BoxConstraints(
                minWidth: 16,
                minHeight: 16,
              ),
              child: Text(
                count > 99 ? '99+' : count.toString(),
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 8,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildHomePage() {
    final postsAsync = ref.watch(postsProvider);
    final isLoading = ref.watch(postsLoadingProvider);
    final error = ref.watch(postsErrorProvider);
    final postController = ref.read(postControllerProvider.notifier);

    return RefreshIndicator(
      color: const Color(0xFF2E7D32),
      onRefresh: () => postController.fetchPosts(),
      child: CustomScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        slivers: [
          const SliverToBoxAdapter(
            child: Padding(
              padding: EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "News Feed",
                  style: TextStyle(
                    color: Color.fromARGB(255, 0, 0, 0),
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
          if (isLoading && postsAsync.isEmpty)
            const SliverFillRemaining(
              child: Center(child: LoadingIndicator()),
            )
          else if (error != null)
            SliverFillRemaining(
              child: ErrorRetryWidget(
                error: error,
                onRetry: () => postController.fetchPosts(),
              ),
            )
          else if (postsAsync.isEmpty)
              const SliverFillRemaining(
                child: Center(
                  child: Text(
                    'No posts available yet 🌱',
                    style: TextStyle(color: Colors.grey),
                  ),
                ),
              )
            else
              SliverList(
                delegate: SliverChildBuilderDelegate(
                      (context, index) {
                    final post = postsAsync[index];
                    return _buildPostCard(post);
                  },
                  childCount: postsAsync.length,
                ),
              ),
          if (isLoading && postsAsync.isNotEmpty)
            const SliverToBoxAdapter(
              child: Padding(
                padding: EdgeInsets.all(16.0),
                child: Center(child: CircularProgressIndicator(color: Color(0xFF2E7D32))),
              ),
            ),
        ],
      ),
    );
  }

Widget _buildPostCard(PostResponse post) {
  return Column(
    crossAxisAlignment: CrossAxisAlignment.start,
    children: [
      // Header section (Profile + Name + Time)
      Padding(
        padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 8.0),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const CircleAvatar(
              backgroundColor: Color(0xFF358128),
              radius: 22,
              child: Icon(Icons.person, color: Colors.white),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    "Department of Agriculture",
                    style: TextStyle(
                      fontWeight: FontWeight.w600,
                      fontSize: 15,
                      color: Colors.black87,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Row(
                    children: [
                      Text(
                        timeago.format(post.createdAt, locale: 'en'),
                        style: const TextStyle(
                          fontSize: 12,
                          color: Colors.black54,
                        ),
                      ),
                      const SizedBox(width: 6),
                      Text(
                        '• ${_formatExactTime(post.createdAt)}',
                        style: const TextStyle(
                          fontSize: 12,
                          color: Colors.grey,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            IconButton(
              onPressed: () {},
              icon: const Icon(Icons.more_horiz, color: Colors.grey),
            ),
          ],
        ),
      ),

      // Post text content
      if (post.content.isNotEmpty)
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 14.0),
          child: Text(
            post.content,
            style: const TextStyle(
              fontSize: 15,
              height: 1.5,
              color: Colors.black87,
            ),
          ),
        ),

      const SizedBox(height: 8),

      // Image section (carousel style)
      if (post.urls.isNotEmpty)
        _buildImageCarousel(post.urls),

      const SizedBox(height: 6),

      // Divider between content and actions
      Padding(
        padding: const EdgeInsets.symmetric(horizontal: 12.0),
        child: Divider(color: Colors.grey[300], thickness: 0.8),
      ),

      // Reaction row (Like, Comment)
      Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            _reactionButton(Icons.thumb_up_alt_outlined, "Like"),
            _reactionButton(Icons.comment_outlined, "Comment"),
          ],
        ),
      ),

      // Spacing between posts
      const SizedBox(height: 10),
      Container(
        height: 6,
        color: const Color(0xFFF0F2F5), // subtle gray background like Facebook
      ),
    ],
  );
}

Widget _reactionButton(IconData icon, String label) {
  return InkWell(
    borderRadius: BorderRadius.circular(8),
    onTap: () {},
    child: Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      child: Row(
        children: [
          Icon(icon, color: Colors.grey[700], size: 20),
          const SizedBox(width: 5),
          Text(
            label,
            style: const TextStyle(
              fontSize: 14,
              color: Colors.black54,
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    ),
  );
}

  Widget _buildImageCarousel(List<String> imageUrls) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: carousel.CarouselSlider(
        options: carousel.CarouselOptions(
          height: 220.0,
          autoPlay: imageUrls.length > 1,
          enlargeCenterPage: true,
          viewportFraction: 0.95,
          aspectRatio: 16 / 9,
        ),
        items: imageUrls.map((url) {
          return ClipRRect(
            borderRadius: BorderRadius.circular(12.0),
            child: CachedNetworkImage(
              imageUrl: url,
              fit: BoxFit.cover,
              width: double.infinity,
              placeholder: (context, _) => Container(
                color: const Color(0xFFE8F5E9),
                child: const Center(
                  child: CircularProgressIndicator(color: Color(0xFF2E7D32), strokeWidth: 2),
                ),
              ),
              errorWidget: (_, __, ___) => const Icon(Icons.broken_image, color: Colors.redAccent),
            ),
          );
        }).toList(),
      ),
    );
  }

  String _formatExactTime(DateTime dateTime) {
    final hours = dateTime.hour.toString().padLeft(2, '0');
    final minutes = dateTime.minute.toString().padLeft(2, '0');
    return '$hours:$minutes';
  }
}