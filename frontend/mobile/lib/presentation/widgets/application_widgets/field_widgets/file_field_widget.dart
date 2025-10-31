import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:image_picker/image_picker.dart'; // Add this import
import 'dart:io'; // For File
import 'package:mobile/data/models/application_data.dart';

/// Widget for FILE field type
class FileFieldWidget extends StatefulWidget {
  final ApplicationField field;
  final PlatformFile? selectedFile;
  final void Function(PlatformFile?) onFileSelected;
  final String? Function(PlatformFile?)? validator;

  const FileFieldWidget({
    super.key,
    required this.field,
    this.selectedFile,
    required this.onFileSelected,
    this.validator,
  });

  @override
  State<FileFieldWidget> createState() => _FileFieldWidgetState();
}

class _FileFieldWidgetState extends State<FileFieldWidget> {
  bool _isUploading = false;
  PlatformFile? _localFile;
  String? _errorMessage;

  static const int maxFileSize = 5242880; // 5MB
  static const allowedMimeTypes = [
    'application/pdf',
    'image/jpeg',
    'image/png',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  ];

  @override
  void initState() {
    super.initState();
    _localFile = widget.selectedFile;
  }

  @override
  void didUpdateWidget(FileFieldWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.selectedFile != widget.selectedFile) {
      setState(() {
        _localFile = widget.selectedFile;
      });
    }
  }

  Future<void> _pickFile() async {
    setState(() {
      _isUploading = true;
      _errorMessage = null;
    });
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.camera);
    if (pickedFile != null) {
      final fileBytes = await pickedFile.readAsBytes();
      final fileSize = fileBytes.length;
      final fileName = pickedFile.name;
      final fileExt = fileName.split('.').last.toLowerCase();

      // Determine mime type
      String? mimeType;
      if (fileExt == 'pdf') {
        mimeType = 'application/pdf';
      } else if (fileExt == 'jpg' || fileExt == 'jpeg') {
        mimeType = 'image/jpeg';
      }
      else if (fileExt == 'png') {
        mimeType = 'image/png';
      }
      else if (fileExt == 'doc') {
        mimeType = 'application/msword';
      }
      else if (fileExt == 'docx') {
        mimeType =
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
      }

      // Validate file type
      if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
        setState(() {
          _errorMessage = 'File type not supported. Allowed types: PDF, JPG, JPEG, PNG, DOC, DOCX.';
          _isUploading = false;
        });
        return;
      }

      // Validate file size
      if (fileSize > maxFileSize) {
        setState(() {
          _errorMessage = 'File size exceeds 5MB limit.';
          _isUploading = false;
        });
        return;
      }

      final platformFile = PlatformFile(
        name: fileName,
        path: pickedFile.path,
        bytes: fileBytes,
        size: fileSize,
      );
      setState(() {
        _localFile = platformFile;
        _isUploading = false;
        _errorMessage = null;
      });
      widget.onFileSelected(platformFile);
      return;
    }
    setState(() {
      _isUploading = false;
    });
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
              title: const Text('File Preview'),
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
    final hasError = widget.validator?.call(_localFile) != null;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.field.fieldName + (widget.field.required ? ' *' : ''),
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w500,
            color: Colors.grey[800],
          ),
        ),
        const SizedBox(height: 8),
        if (_isUploading)
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 8),
            child: Row(
              children: [
                const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                ),
                const SizedBox(width: 12),
                Text('Uploading...', style: TextStyle(color: Colors.grey[700])),
              ],
            ),
          ),
        if (_errorMessage != null)
          Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: Text(
              _errorMessage!,
              style: TextStyle(color: Colors.red, fontSize: 13),
            ),
          ),
        Container(
          decoration: BoxDecoration(
            border: Border.all(
              color: hasError ? Colors.red : Colors.grey[300]!,
            ),
            borderRadius: BorderRadius.circular(12),
            color: Colors.white,
          ),
          child: ListTile(
            leading: _localFile != null && _localFile!.bytes != null
                ? ClipRRect(
                    borderRadius: BorderRadius.circular(6),
                    child: Image.memory(
                      _localFile!.bytes!,
                      width: 40,
                      height: 40,
                      fit: BoxFit.cover,
                    ),
                  )
                : Icon(
                    _localFile != null
                        ? Icons.check_circle
                        : Icons.attach_file,
                    color: _localFile != null
                        ? Colors.green
                        : Theme.of(context).primaryColor,
                  ),
            title: Text(
              _localFile != null
                  ? _localFile!.name
                  : 'Tap to upload',
              style: TextStyle(
                color: _localFile != null
                    ? Colors.green[700]
                    : Colors.grey[700],
              ),
            ),
            trailing: _localFile != null
                ? Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      IconButton(
                        icon: const Icon(Icons.visibility, color: Colors.blue),
                        onPressed: _showPreview,
                      ),
                      IconButton(
                        icon: const Icon(Icons.close, color: Colors.red),
                        onPressed: () {
                          setState(() {
                            _localFile = null;
                          });
                          widget.onFileSelected(null);
                        },
                      ),
                    ],
                  )
                : null,
            onTap: _isUploading ? null : _pickFile,
          ),
        ),
        if (hasError)
          Padding(
            padding: const EdgeInsets.only(left: 16, top: 8),
            child: Text(
              widget.validator!.call(_localFile)!,
              style: TextStyle(
                color: Theme.of(context).colorScheme.error,
                fontSize: 12,
              ),
            ),
          ),
      ],
    );
  }
}
