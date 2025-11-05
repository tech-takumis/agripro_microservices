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

import 'application_page.dart';
import 'contact_department_page.dart';
import 'profile_page.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<HomePage> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  int _currentIndex = 0;
  late PageController _pageController;

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

    return Scaffold(
      backgroundColor: const Color(0xFFF6FAF5),
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
          const ProfilePage(),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: const Color(0xFF2E7D32),
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
              builder: (context) => const ContactDepartmentPage(serviceType: 'Chat with Agriculturist'),
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
            setState(() {
              _currentIndex = index;
              _pageController.jumpToPage(index);
            });
          },
          indicatorColor: const Color(0xFF2E7D32),
          unselectedItemColor: const Color(0xFF9E9E9E),
          selectedItemColor: const Color(0xFFFFEB3B),
          backgroundColor: Colors.white.withOpacity(0.95),
          items: [
            CrystalNavigationBarItem(icon: Icons.home_rounded),
            CrystalNavigationBarItem(icon: Icons.assignment_rounded),
            CrystalNavigationBarItem(icon: Icons.person_rounded),
          ],
        ),
      ),
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
                    color: Color(0xFF2E7D32),
                    fontSize: 22,
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
  return Container(
    margin: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 8.0),
    decoration: BoxDecoration(
      color: Colors.white,
      borderRadius: BorderRadius.circular(16.0),
      boxShadow: [
        BoxShadow(
          color: Colors.green.withOpacity(0.15),
          blurRadius: 5,
          offset: const Offset(0, 3),
        ),
      ],
    ),
    child: Padding(
      padding: const EdgeInsets.all(12.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Profile row: user/admin + time
          Row(
            children: [
              // Profile Icon
              const CircleAvatar(
                backgroundColor: Color(0xFFE8F5E9),
                radius: 22,
                child: Icon(Icons.person, color: Color(0xFF2E7D32)),
              ),
              const SizedBox(width: 10),

              // User Information
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
// Poster name (default only)
Text(
  "Department of Agriculture",
  style: const TextStyle(
    fontSize: 15,
    fontWeight: FontWeight.w600,
    color: Colors.black87,
  ),
),

                    // Date & Time
                    Row(
                      children: [
                        Text(
                          timeago.format(post.createdAt, locale: 'en'),
                          style: const TextStyle(
                            fontSize: 12,
                            color: Colors.grey,
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
            ],
          ),

          const SizedBox(height: 12),

          // Post content
          Text(
            post.content,
            style: const TextStyle(
              fontSize: 15,
              color: Colors.black87,
              height: 1.4,
            ),
          ),

          // Post images (if any)
          if (post.urls.isNotEmpty) _buildImageCarousel(post.urls),

          const SizedBox(height: 10),
          const Divider(thickness: 0.8),

          // Reaction buttons
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 6.0, vertical: 4.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _reactionButton(Icons.thumb_up_alt_outlined, "React"),
                _reactionButton(Icons.comment_outlined, "Comment"),
              ],
            ),
          ),
        ],
      ),
    ),
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
            Icon(icon, color: Colors.grey[600], size: 20),
            const SizedBox(width: 5),
            Text(
              label,
              style: const TextStyle(
                fontSize: 14,
                color: Colors.grey,
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
}

String _formatExactTime(DateTime dateTime) {
  final hours = dateTime.hour.toString().padLeft(2, '0');
  final minutes = dateTime.minute.toString().padLeft(2, '0');
  return '$hours:$minutes';
}
