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
    
    // Initialize timeago messages
    timeago.setLocaleMessages('en', timeago.EnMessages());
    
    // Fetch posts when the page initializes
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

  // Removed duplicate initState

  Widget _buildHomePage() {
    final postsAsync = ref.watch(postsProvider);
    final isLoading = ref.watch(postsLoadingProvider);
    final error = ref.watch(postsErrorProvider);
    final postController = ref.read(postControllerProvider.notifier);

    return RefreshIndicator(
      onRefresh: () => postController.fetchPosts(),
      child: CustomScrollView(
        slivers: [
          SliverAppBar(
            title: const Text('Agriculturist Posts'),
            floating: true,
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
                child: Text('No posts yet. Be the first to post!'),
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
                child: Center(child: CircularProgressIndicator()),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildPostCard(PostResponse post) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Post Header
          ListTile(
            leading: CircleAvatar(
              backgroundColor: Theme.of(context).primaryColor,
              child: Text(
                post.authorName.isNotEmpty ? post.authorName[0].toUpperCase() : '?',
                style: const TextStyle(color: Colors.white),
              ),
            ),
            title: Text(
              post.authorName,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text(timeago.format(post.createdAt)),
            trailing: PopupMenuButton<String>(
              itemBuilder: (context) => [
                const PopupMenuItem(
                  value: 'report',
                  child: Text('Report Post'),
                ),
              ],
              onSelected: (value) {
                // Handle menu item selection
              },
            ),
          ),
          
          // Post Content
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
            child: Text(post.content),
          ),
          
          // Image Carousel
          if (post.urls.isNotEmpty) _buildImageCarousel(post.urls),
        ],
      ),
    );
  }
  
  Widget _buildImageCarousel(List<String> imageUrls) {
    // Debug: Print the received image URLs
    debugPrint('Image URLs received: $imageUrls');

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: carousel.CarouselSlider(
        options: carousel.CarouselOptions(
          height: 250.0,
          autoPlay: imageUrls.length > 1,
          enlargeCenterPage: true,
          viewportFraction: 1.0,
          aspectRatio: 1.0,
          onPageChanged: (index, reason) {
            debugPrint('Page changed to index $index');
          },
        ),
        items: imageUrls.map((imageUrl) {
          // Use the original URL as is, including all query parameters
          debugPrint('Using image URL: $imageUrl');

          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4.0),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(8.0),
              child: CachedNetworkImage(
                imageUrl: imageUrl,
                fit: BoxFit.cover,
                width: double.infinity,
                maxHeightDiskCache: 1000, // Increase cache size if needed
                memCacheHeight: 1000, // Cache higher resolution images
                httpHeaders: const {
                  'Accept': 'image/*',
                },
                placeholder: (context, url) => Container(
                  color: Colors.grey[200],
                  child: const Center(
                    child: SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    ),
                  ),
                ),
                errorWidget: (context, url, error) {
                  debugPrint('Error loading image: $error\nURL: $url');
                  return Container(
                    color: Colors.grey[200],
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Icon(Icons.error_outline, color: Colors.red, size: 30),
                        const SizedBox(height: 8),
                        Text(
                          'Failed to load image',
                          style: TextStyle(
                            color: Colors.grey[600],
                            fontSize: 12,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}
