import 'dart:io';
import 'package:dio/dio.dart';
import 'package:mime/mime.dart';
import 'package:image/image.dart' as img;
import 'package:http_parser/http_parser.dart';
import '../models/document_response.dart';
import 'storage_service.dart';
import 'package:mobile/injection_container.dart'; // For getIt
import 'package:mobile/presentation/controllers/auth_controller.dart'; // Add this import
import 'package:file_picker/file_picker.dart';

/// Service for handling document uploads
class DocumentService {
  final Dio _dio = Dio();
  final String baseUrl = 'http://localhost:9001/api/v1';

  /// Compress image before upload
  static Future<File> compressImage(File file) async {
    final image = img.decodeImage(await file.readAsBytes());
    if (image == null) return file; // fallback if decode fails

    // Resize to max width 1024 while preserving aspect ratio
    final resized = img.copyResize(image, width: 1024);

    // Save as JPG with 80% quality
    final compressedBytes = img.encodeJpg(resized, quality: 80);

    final ext = file.path.split('.').last;
    final newPath = file.path.replaceFirst('.$ext', '_compressed.jpg');
    final compressedFile = File(newPath);

    await compressedFile.writeAsBytes(compressedBytes);
    return compressedFile;
  }

  /// Upload a document file
  Future<DocumentResponse> uploadDocument({
    required AuthState authState,
    required PlatformFile file,
  }) async {
    try {
      // Use token from authState
      final token = authState.token;
      final refreshToken = getIt<StorageService>().getRefreshToken();

      // Prepare a File object from PlatformFile
      File fileObj;
      if (file.path != null) {
        fileObj = File(file.path!);
      } else if (file.bytes != null) {
        // Write bytes to a temp file
        final tempDir = Directory.systemTemp;
        final tempFile = await File('${tempDir.path}/${file.name}').create();
        await tempFile.writeAsBytes(file.bytes!);
        fileObj = tempFile;
      } else {
        throw Exception('PlatformFile must have either a path or bytes');
      }

      // Detect MIME type
      final mimeType = lookupMimeType(fileObj.path) ?? 'application/octet-stream';

      // ✅ Compress only if it's an image
      File fileToUpload = fileObj;
      if (mimeType.startsWith('image/')) {
        print('Compressing image: ${fileObj.path}');
        fileToUpload = await compressImage(fileObj);
        print('Compressed image path: ${fileToUpload.path}');
      }

      print('Uploading document:');
      print('  file: ${fileToUpload.path}');
      print('  mimeType: $mimeType');

      // Prepare multipart form data
      final multipartFile = await MultipartFile.fromFile(
        fileToUpload.path,
        filename: fileToUpload.path.split('/').last,
        contentType: MediaType.parse(mimeType),
      );
      print('MultipartFile:');
      print('  path: ${fileToUpload.path}');
      print('  filename: ${multipartFile.filename}');
      print('  contentType: $mimeType');

      final formData = FormData();
      formData.files.add(MapEntry('file', multipartFile));

      print('FormData fields after construction:');
      for (final field in formData.fields) {
        print('  ${field.key}: ${field.value}');
      }
      for (final file in formData.files) {
        print('  file: ${file.key}, filename: ${file.value.filename}');
      }

      // Make the upload request
      final response = await _dio.post(
        '$baseUrl/documents',
        data: formData,
        options: Options(
          headers: {
            'Authorization': 'Bearer $token',
            if (refreshToken != null) 'X-Refresh-Token': refreshToken,
          },
        ),
      );

      print('Response status: ${response.statusCode}');
      print('Response body: ${response.data}');

      if (response.statusCode == 200 || response.statusCode == 201) {
        return DocumentResponse.fromJson(response.data);
      } else {
        print('Upload failed: ${response.statusMessage} - ${response.data}');
        throw Exception(
          'Failed to upload document: ${response.statusMessage} - ${response.data}',
        );
      }
    } on DioException catch (e) {
      print('DioException: $e');
      if (e.response != null) {
        print('DioException response: ${e.response?.data}');
        throw Exception(
          'Upload failed: ${e.response?.data['message'] ?? e.message} - ${e.response?.data}',
        );
      } else {
        throw Exception('Network error: ${e.message}');
      }
    } catch (e) {
      print('Unexpected error during upload: $e');
      throw Exception('Unexpected error during upload: $e');
    }
  }
}
