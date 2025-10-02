import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:mobile/data/models/application_data.dart';
import 'package:image/image.dart' as img;

enum UploadStatus { idle, uploading, success, failure }

class SignatureFieldWidget extends StatefulWidget {
  final ApplicationField field;
  final XFile? signatureFile;
  final void Function(XFile?) onSignatureChanged;
  final String? Function(XFile?)? validator;

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
  final ImagePicker _picker = ImagePicker();
  UploadStatus _uploadStatus = UploadStatus.idle;
  String? _errorMessage;
  XFile? _localFile; // ✅ track locally for immediate UI refresh

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

  Future<bool> _requestPermission(Permission permission) async {
    final status = await permission.request();
    return status.isGranted;
  }

  Future<void> _pickFromCamera() async {
    if (!await _requestPermission(Permission.camera)) {
      _showSnack('Camera permission denied');
      return;
    }

    try {
      final XFile? pickedFile = await _picker.pickImage(
        source: ImageSource.camera,
        imageQuality: 85,
        preferredCameraDevice: CameraDevice.rear,
      );
      if (pickedFile != null) _handleFilePicked(pickedFile);
    } catch (e) {
      _showSnack('Failed to capture image: $e');
    }
  }

  Future<void> _pickFromGallery() async {
    if (!await _requestPermission(Permission.photos)) {
      _showSnack('Gallery permission denied');
      return;
    }

    try {
      final XFile? pickedFile = await _picker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 85,
      );
      if (pickedFile != null) _handleFilePicked(pickedFile);
    } catch (e) {
      _showSnack('Failed to select image: $e');
    }
  }

  void _handleFilePicked(XFile file) async {
    setState(() {
      _uploadStatus = UploadStatus.uploading;
      _errorMessage = null;
    });

    try {
      final f = File(file.path);
      final length = await f.length();

      if (length < 1024) throw Exception("Selected image is too small or invalid.");

      final decodedImage = img.decodeImage(await f.readAsBytes());
      if (decodedImage == null || decodedImage.width < 50 || decodedImage.height < 50) {
        throw Exception("Selected image is corrupted or unsupported format.");
      }

      // ✅ simulate upload
      await Future.delayed(const Duration(seconds: 2));

      // ✅ update local state immediately
      setState(() {
        _uploadStatus = UploadStatus.success;
        _localFile = file;
      });

      // ✅ notify parent
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

  void _showPickerOptions() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) => SafeArea(
        child: Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.camera_alt, color: Colors.blue),
              title: const Text('Take Photo'),
              onTap: () {
                Navigator.pop(context);
                _pickFromCamera();
              },
            ),
            ListTile(
              leading: const Icon(Icons.photo_library, color: Colors.green),
              title: const Text('Choose from Gallery'),
              onTap: () {
                Navigator.pop(context);
                _pickFromGallery();
              },
            ),
          ],
        ),
      ),
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
              child: Image.file(File(_localFile!.path), fit: BoxFit.contain),
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
            onTap: _showPickerOptions,
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
            child: Image.file(File(_localFile!.path), height: 100, fit: BoxFit.contain),
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
