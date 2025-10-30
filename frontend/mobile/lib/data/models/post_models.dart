class PostResponse {
  final String id;
  final String title;
  final String content;
  final String authorId;
  final String authorName;
  final List<String> urls;
  final DateTime createdAt;
  final DateTime updatedAt;

  PostResponse({
    required this.id,
    required this.title,
    required this.content,
    required this.authorId,
    required this.authorName,
    required this.urls,
    required this.createdAt,
    required this.updatedAt,
  });

  factory PostResponse.fromJson(Map<String, dynamic> json) {
    return PostResponse(
      id: json['id'] as String,
      title: json['title'] as String? ?? '',
      content: json['content'] as String? ?? '',
      authorId: json['authorId'] as String? ?? '',
      authorName: json['authorName'] as String? ?? 'Unknown',
      urls: List<String>.from(json['urls'] as List<dynamic>? ?? []),
      createdAt: DateTime.parse(json['createdAt'] as String).toLocal(),
      updatedAt: DateTime.parse(json['updatedAt'] as String).toLocal(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'content': content,
      'authorId': authorId,
      'authorName': authorName,
      'urls': urls,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }
}

class PostPageResponse {
  final List<PostResponse> posts;
  final String? nextCursor;
  final bool hasMore;

  PostPageResponse({
    required this.posts,
    this.nextCursor,
    required this.hasMore,
  });

  factory PostPageResponse.fromJson(Map<String, dynamic> json) {
    return PostPageResponse(
      posts: (json['posts'] as List<dynamic>? ?? [])
          .map((item) => PostResponse.fromJson(item as Map<String, dynamic>))
          .toList(),
      nextCursor: json['nextCursor'] as String?,
      hasMore: json['hasMore'] as bool? ?? false,
    );
  }
}

class CreatePostRequest {
  final String content;
  final List<String>? fileIds;

  CreatePostRequest({
    required this.content,
    this.fileIds,
  });

  Map<String, dynamic> toJson() {
    return {
      'content': content,
      if (fileIds != null) 'fileIds': fileIds,
    };
  }
}
class PostState {
  final List<PostResponse> posts;
  final bool isLoading;
  final String? error;
  final String? nextCursor;
  final bool hasNext;

  PostState({
    this.posts = const [],
    this.isLoading = false,
    this.error,
    this.nextCursor,
    this.hasNext = false,
  });

  PostState copyWith({
    List<PostResponse>? posts,
    bool? isLoading,
    String? error,
    String? nextCursor,
    bool? hasNext,
  }) {
    return PostState(
      posts: posts ?? this.posts,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      nextCursor: nextCursor ?? this.nextCursor,
      hasNext: hasNext ?? this.hasNext,
    );
  }
}
