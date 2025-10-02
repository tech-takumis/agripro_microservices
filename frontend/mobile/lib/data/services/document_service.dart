import 'dart:io';
import 'package:dio/dio.dart';
import 'package:get/get.dart' as getx;
import 'package:mime/mime.dart';
import 'package:image/image.dart' as img;
import 'package:http_parser/http_parser.dart';
import '../models/document_response.dart';
import 'api_service.dart';
import 'storage_service.dart';

/// Service for handling document uploads
class DocumentService extends getx.GetxService {
  static DocumentService get to => getx.Get.find();

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
    required String referenceId,
    required File file,
    required String documentType,
    String? metaData,
  }) async {
    try {
      // Get auth token from StorageService
      final token = StorageService.to.getToken();
      final refreshToken = StorageService.to.getRefreshToken();

      // Detect MIME type
      final mimeType = lookupMimeType(file.path) ?? 'application/octet-stream';

      // ✅ Compress only if it's an image
      File fileToUpload = file;
      if (mimeType.startsWith('image/')) {
        print('Compressing image: ${file.path}');
        fileToUpload = await compressImage(file);
        print('Compressed image path: ${fileToUpload.path}');
      }

      print('Uploading document:');
      print('  referenceId: $referenceId');
      print('  file: ${fileToUpload.path}');
      print('  documentType: $documentType');
      print('  mimeType: $mimeType');
      print('  metaData: $metaData');

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
      formData.fields.add(MapEntry('referenceId', referenceId));
      formData.fields.add(MapEntry('documentType', documentType));
      if (metaData != null) {
        formData.fields.add(MapEntry('metaData', metaData));
      }
      formData.files.add(MapEntry('file', multipartFile));

      print('FormData fields after construction:');
      formData.fields.forEach((f) => print('  ${f.key}: ${f.value}'));
      formData.files.forEach((f) => print('  file: ${f.key}, filename: ${f.value.filename}'));

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

  /// Upload multiple documents
  Future<List<DocumentResponse>> uploadMultipleDocuments({
    required String referenceId,
    required Map<String, File> files,
  }) async {
    final List<DocumentResponse> uploadedDocuments = [];

    for (final entry in files.entries) {
      final documentType = entry.key;
      final file = entry.value;

      final response = await uploadDocument(
        referenceId: referenceId,
        file: file,
        documentType: documentType,
      );

      uploadedDocuments.add(response);
    }

    return uploadedDocuments;
  }
}
