import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';

class MessageInputField extends StatefulWidget {
  final void Function(String text) onSend;
  final void Function(PlatformFile file)? onFileSend; // New callback

  const MessageInputField({
    super.key,
    required this.onSend,
    this.onFileSend,
  });

  @override
  State<MessageInputField> createState() => _MessageInputFieldState();
}

class _MessageInputFieldState extends State<MessageInputField> {
  final TextEditingController _controller = TextEditingController();

  void _handleSend() {
    final text = _controller.text.trim();
    if (text.isNotEmpty) {
      widget.onSend(text);
      _controller.clear();
    }
  }

  Future<void> _handleFilePick() async {
    final result = await FilePicker.platform.pickFiles();
    if (result != null && result.files.isNotEmpty) {
      final file = result.files.first;
      if (widget.onFileSend != null) {
        widget.onFileSend!(file);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Row(
        children: [
          IconButton(
            icon: const Icon(Icons.attach_file),
            onPressed: _handleFilePick,
          ),
          Expanded(
            child: TextField(
              controller: _controller,
              decoration: const InputDecoration(
                hintText: 'Type a message...',
                contentPadding: EdgeInsets.all(12),
              ),
              onSubmitted: (_) => _handleSend(),
            ),
          ),
          IconButton(
            icon: const Icon(Icons.send),
            onPressed: _handleSend,
          ),
        ],
      ),
    );
  }
}
