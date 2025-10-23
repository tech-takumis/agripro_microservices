import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import '../../../../data/models/application_data.dart';

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
  Future<void> _pickFile() async {
    final result = await FilePicker.platform.pickFiles(type: FileType.any);
    if (result != null && result.files.isNotEmpty) {
      widget.onFileSelected(result.files.first);
    }
  }

  @override
  Widget build(BuildContext context) {
    final hasError = widget.validator?.call(widget.selectedFile) != null;

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
        Container(
          decoration: BoxDecoration(
            border: Border.all(
              color: hasError ? Colors.red : Colors.grey[300]!,
            ),
            borderRadius: BorderRadius.circular(12),
            color: Colors.white,
          ),
          child: ListTile(
            leading: Icon(
              widget.selectedFile != null
                  ? Icons.check_circle
                  : Icons.attach_file,
              color: widget.selectedFile != null
                  ? Colors.green
                  : Theme.of(context).primaryColor,
            ),
            title: Text(
              widget.selectedFile != null
                  ? widget.selectedFile!.name
                  : 'Tap to select file',
              style: TextStyle(
                color: widget.selectedFile != null
                    ? Colors.green[700]
                    : Colors.grey[700],
              ),
            ),
            trailing: widget.selectedFile != null
                ? IconButton(
                    icon: const Icon(Icons.close, color: Colors.red),
                    onPressed: () => widget.onFileSelected(null),
                  )
                : null,
            onTap: _pickFile,
          ),
        ),
        if (hasError)
          Padding(
            padding: const EdgeInsets.only(left: 16, top: 8),
            child: Text(
              widget.validator!.call(widget.selectedFile)!,
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
