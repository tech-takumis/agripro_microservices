import 'dart:io';
import 'package:dio/dio.dart';
import 'package:mime/mime.dart';
import 'package:http_parser/http_parser.dart';
import 'package:mobile/data/models/post_models.dart';

class PostApiService {
  final Dio _dio;
  final String baseUrl;
  static const String path = '/posts';

  PostApiService(this._dio, {required this.baseUrl}) {
    _dio.options.baseUrl = baseUrl;
  }

  Future<PostPageResponse> fetchPosts({
    String? cursor,
    int limit = 10,
  }) async {
    try {
      final response = await _dio.get(
        path,
        queryParameters: {
          if (cursor != null) 'cursor': cursor,
          'limit': limit,
        },
      );

      // Map the API response to our model
      final responseData = response.data as Map<String, dynamic>;
      return PostPageResponse(
        posts: (responseData['posts'] as List)
            .map((post) => PostResponse.fromJson(post))
            .toList(),
        nextCursor: responseData['nextCursor'],
        hasMore: responseData['hasMore'] ?? false,
      );
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
        path,
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
