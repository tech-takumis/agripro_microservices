import 'dart:io';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/data/models/post_models.dart';
import 'package:mobile/data/services/post_api_service.dart';
import 'package:mobile/injection_container.dart';

// Provider for PostApiService
final postApiServiceProvider = Provider<PostApiService>((ref) {
  return getIt<PostApiService>();
});

// State class for post state
class PostState {
  final List<PostResponse> posts;
  final bool isLoading;
  final String? error;
  final bool isCreating;
  final bool hasMore;
  final bool hasNext;
  final String? nextCursor;

  const PostState({
    this.posts = const [],
    this.isLoading = false,
    this.error,
    this.isCreating = false,
    this.hasMore = true,
    this.hasNext = false,
    this.nextCursor,
  });

  PostState copyWith({
    List<PostResponse>? posts,
    bool? isLoading,
    String? error,
    bool? isCreating,
    bool? hasMore,
    bool? hasNext,
    String? nextCursor,
  }) {
    return PostState(
      posts: posts ?? this.posts,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      isCreating: isCreating ?? this.isCreating,
hasMore: hasMore ?? this.hasMore,
      hasNext: hasNext ?? this.hasNext,
      nextCursor: nextCursor ?? this.nextCursor,
    );
  }
}

// PostController Notifier
class PostController extends StateNotifier<PostState> {
  final Ref _ref;
  
  PostController(this._ref) : super(const PostState());

  PostApiService get _postService => _ref.read(postApiServiceProvider);

  Future<void> fetchPosts({String? cursor, int limit = 10}) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final response = await _postService.fetchPosts(
        cursor: cursor,
        limit: limit,
      );
      
      state = state.copyWith(
        posts: cursor == null 
            ? response.posts 
            : [...state.posts, ...response.posts],
        isLoading: false,
        hasMore: response.hasMore,
        nextCursor: response.nextCursor,
        hasNext: response.hasMore,
      );
    } catch (e) {
      state = state.copyWith(
        error: e.toString(),
        isLoading: false,
      );
      rethrow;
    }
  }

  Future<void> createPost({
    required String content,
    List<File>? files,
  }) async {
    state = state.copyWith(isCreating: true, error: null);

    try {
      final newPost = await _postService.createPost(
        content: content,
        files: files,
      );
      
      state = state.copyWith(
        posts: [newPost, ...state.posts],
        isCreating: false,
      );
    } catch (e) {
      state = state.copyWith(
        error: e.toString(),
        isCreating: false,
      );
      rethrow;
    }
  }

  // Helper method to get a post by ID
  PostResponse? getPostById(String id) {
    try {
      return state.posts.firstWhere((post) => post.id == id);
    } catch (_) {
      return null;
    }
  }
  
  // Load more posts for pagination
  Future<void> loadMorePosts() async {
    if (state.isLoading || !state.hasMore) return;
    
    await fetchPosts(
      cursor: state.nextCursor,
    );
  }

  // Clear any error state
  void clearError() {
    if (state.error != null) {
      state = state.copyWith(error: null);
    }
  }
}

// Provider for PostController
final postControllerProvider = StateNotifierProvider<PostController, PostState>((ref) {
  return PostController(ref);
});

// Provider for the list of posts
final postsProvider = Provider<List<PostResponse>>((ref) {
  return ref.watch(postControllerProvider.select((state) => state.posts));
});

// Provider for loading state
final postsLoadingProvider = Provider<bool>((ref) {
  return ref.watch(postControllerProvider.select((state) => state.isLoading));
});

// Provider for error state
final postsErrorProvider = Provider<String?>((ref) {
  return ref.watch(postControllerProvider.select((state) => state.error));
});

// Provider for post creation state
final postCreatingProvider = Provider<bool>((ref) {
  return ref.watch(postControllerProvider.select((state) => state.isCreating));
});