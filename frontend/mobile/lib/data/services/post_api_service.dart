import 'dart:io';
import 'package:dio/dio.dart';
import 'package:mime/mime.dart';
import 'package:http_parser/http_parser.dart';
import 'package:mobile/data/models/post_models.dart';
class PostPageResponse {
  final List<PostResponse> content;
  final int totalElements;
  final int totalPages;
  final bool last;
  final int size;
  final int number;
  final bool first;
  final int numberOfElements;
  final bool empty;
  final String? cursor;

  PostPageResponse({
    required this.content,
    required this.totalElements,
    required this.totalPages,
    required this.last,
    required this.size,
    required this.number,
    required this.first,
    required this.numberOfElements,
    required this.empty,
    this.cursor,
  });

  factory PostPageResponse.fromJson(Map<String, dynamic> json) {
    return PostPageResponse(
      content: (json['content'] as List)
          .map((post) => PostResponse.fromJson(post))
          .toList(),
      totalElements: json['totalElements'] as int,
      totalPages: json['totalPages'] as int,
      last: json['last'] as bool,
      size: json['size'] as int,
      number: json['number'] as int,
      first: json['first'] as bool,
      numberOfElements: json['numberOfElements'] as int,
      empty: json['empty'] as bool,
      cursor: json['cursor'] as String?,
    );
  }
}

class PostApiService {
  final Dio _dio;
  static const String basePath = '/api/v1/posts';

  PostApiService(this._dio);

  Future<PostPageResponse> fetchPosts({
    String? cursor,
    int limit = 10,
  }) async {
    try {
      final response = await _dio.get(
        basePath,
        queryParameters: {
          if (cursor != null) 'cursor': cursor,
          'limit': limit,
        },
      );

      return PostPageResponse.fromJson(response.data);
    } on DioException catch (e) {
      throw Exception('Failed to load posts: ${e.message}');
    }
  }

  Future<PostResponse> createPost({
    required String content,
    List<File>? files,
  }) async {
    try {
      final formData = FormData.fromMap({
        'content': content,
        if (files != null && files.isNotEmpty)
          'files': await _prepareFiles(files),
      });

      final response = await _dio.post(
        basePath,
        data: formData,
        options: Options(
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        ),
      );

      return PostResponse.fromJson(response.data);
    } on DioException catch (e) {
      throw Exception('Failed to create post: ${e.message}');
    }
  }

  Future<List<MultipartFile>> _prepareFiles(List<File> files) async {
    final List<MultipartFile> multipartFiles = [];
    
    for (final file in files) {
      final fileName = file.path.split('/').last;
      final mimeType = lookupMimeType(file.path) ?? 'application/octet-stream';
      
      multipartFiles.add(await MultipartFile.fromFile(
        file.path,
        filename: fileName,
        contentType: MediaType.parse(mimeType),
      ));
    }
    
    return multipartFiles;
  }
}
