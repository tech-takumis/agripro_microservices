import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:image/image.dart' as img;
import 'package:mime/mime.dart';

enum UploadStatus { idle, uploading, success, failure }

class SignatureFieldWidget extends StatefulWidget {
  final ApplicationField field;
  final PlatformFile? signatureFile;
  final void Function(PlatformFile?) onSignatureChanged;
  final String? Function(PlatformFile?)? validator;

  const SignatureFieldWidget({
    Key? key,
    required this.field,
    required this.signatureFile,
    required this.onSignatureChanged,
    this.validator,
  }) : super(key: key);

  @override
  State<SignatureFieldWidget> createState() => _SignatureFieldWidgetState();
}

class _SignatureFieldWidgetState extends State<SignatureFieldWidget> {
  UploadStatus _uploadStatus = UploadStatus.idle;
  String? _errorMessage;
  PlatformFile? _localFile;

  @override
  void initState() {
    super.initState();
    _localFile = widget.signatureFile;
  }

  @override
  void didUpdateWidget(SignatureFieldWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.signatureFile != widget.signatureFile) {
      setState(() {
        _localFile = widget.signatureFile;
      });
    }
  }

  Future<void> _pickSignatureFile() async {
    try {
      final result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['png', 'jpg', 'jpeg'],
        withData: true,
      );
      if (result != null && result.files.isNotEmpty) {
        final file = result.files.first;
        await _handleFilePicked(file);
      }
    } catch (e) {
      _showSnack('Failed to pick file: $e');
    }
  }

  Future<void> _handleFilePicked(PlatformFile file) async {
    setState(() {
      _uploadStatus = UploadStatus.uploading;
      _errorMessage = null;
    });

    try {
      if (file.size < 1024) throw Exception("Selected image is too small or invalid.");

      final bytes = file.bytes ?? await File(file.path!).readAsBytes();
      final decodedImage = img.decodeImage(bytes);
      if (decodedImage == null || decodedImage.width < 50 || decodedImage.height < 50) {
        throw Exception("Selected image is corrupted or unsupported format.");
      }

      // Check mime type
      final mimeType = lookupMimeType(file.path ?? file.name) ?? '';
      if (mimeType == 'application/octet-stream') {
        setState(() {
          _uploadStatus = UploadStatus.failure;
          _errorMessage = "The selected file/image is not supported. Please select a valid image file (PNG, JPG, JPEG).";
        });
        _showSnack(_errorMessage!);
        return;
      }

      // Simulate upload
      await Future.delayed(const Duration(seconds: 2));

      setState(() {
        _uploadStatus = UploadStatus.success;
        _localFile = file;
      });

      widget.onSignatureChanged(file);
    } catch (e) {
      setState(() {
        _uploadStatus = UploadStatus.failure;
        _errorMessage = e.toString();
      });
    }
  }

  void _showSnack(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  void _showPreview() {
    if (_localFile == null) return;
    showDialog(
      context: context,
      builder: (context) => Dialog(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            AppBar(
              title: const Text('Signature Preview'),
              automaticallyImplyLeading: false,
              actions: [
                IconButton(icon: const Icon(Icons.close), onPressed: () => Navigator.pop(context)),
              ],
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: _localFile!.bytes != null
                  ? Image.memory(_localFile!.bytes!, fit: BoxFit.contain)
                  : (_localFile!.path != null
                      ? Image.file(File(_localFile!.path!), fit: BoxFit.contain)
                      : const Text('No preview available')),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final hasError = widget.validator?.call(_localFile) != null || _uploadStatus == UploadStatus.failure;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.field.fieldName + (widget.field.required ? ' *' : ''),
          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
        ),
        const SizedBox(height: 8),
        Container(
          decoration: BoxDecoration(
            border: Border.all(
              color: hasError ? Colors.red : Colors.grey,
              width: hasError ? 2 : 1,
            ),
            borderRadius: BorderRadius.circular(12),
          ),
          child: InkWell(
            onTap: _pickSignatureFile,
            borderRadius: BorderRadius.circular(12),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  if (_uploadStatus == UploadStatus.uploading)
                    const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  else
                    Icon(
                      _localFile != null
                          ? (_uploadStatus == UploadStatus.success ? Icons.check_circle : Icons.error)
                          : Icons.edit,
                      color: _localFile != null
                          ? (_uploadStatus == UploadStatus.success ? Colors.green : Colors.red)
                          : Colors.blue,
                    ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      _uploadStatus == UploadStatus.uploading
                          ? "Uploading..."
                          : _localFile != null
                              ? (_uploadStatus == UploadStatus.success ? "Signature uploaded" : "Upload failed")
                              : "Upload signature",
                      style: TextStyle(
                        fontWeight: FontWeight.w500,
                        color: _uploadStatus == UploadStatus.failure
                            ? Colors.red
                            : (_localFile != null ? Colors.green[700] : Colors.grey[700]),
                      ),
                    ),
                  ),
                  if (_localFile != null && _uploadStatus == UploadStatus.success) ...[
                    IconButton(icon: const Icon(Icons.visibility, color: Colors.blue), onPressed: _showPreview),
                    IconButton(
                      icon: const Icon(Icons.close, color: Colors.red),
                      onPressed: () {
                        setState(() {
                          _localFile = null;
                          _uploadStatus = UploadStatus.idle;
                        });
                        widget.onSignatureChanged(null);
                      },
                    ),
                  ],
                ],
              ),
            ),
          ),
        ),
        if (_localFile != null && _uploadStatus == UploadStatus.success)
          Container(
            margin: const EdgeInsets.only(top: 8),
            child: _localFile!.bytes != null
                ? Image.memory(_localFile!.bytes!, height: 100, fit: BoxFit.contain)
                : (_localFile!.path != null
                    ? Image.file(File(_localFile!.path!), height: 100, fit: BoxFit.contain)
                    : const Text('No preview available')),
          ),
        if (hasError)
          Padding(
            padding: const EdgeInsets.only(top: 8, left: 16),
            child: Text(
              _errorMessage ?? widget.validator?.call(_localFile) ?? "Upload failed",
              style: TextStyle(color: Theme.of(context).colorScheme.error, fontSize: 12),
            ),
          ),
      ],
    );
  }
}
