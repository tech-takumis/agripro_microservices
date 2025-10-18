import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile/features/messages/providers/message_provider.dart';
import 'package:mobile/presentation/widgets/message_bubble.dart';
import 'package:mobile/presentation/widgets/message_input_field.dart';
import 'package:mobile/data/models/message.dart';
import 'package:file_picker/file_picker.dart';

class ContactDepartmentPage extends ConsumerStatefulWidget {
  final String serviceType;
  const ContactDepartmentPage({super.key, required this.serviceType});

  @override
  ConsumerState<ContactDepartmentPage> createState() => _ContactDepartmentPageState();
}

class _ContactDepartmentPageState extends ConsumerState<ContactDepartmentPage> {
  final ScrollController _scrollController = ScrollController();

  // Holds uploaded files (max 5)
  final List<PlatformFile> _uploadedFiles = [];

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      });
    }
  }

  void _handleFileSend(PlatformFile file) {
    if (_uploadedFiles.length >= 5) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('You can upload up to 5 files only.')),
      );
      return;
    }
    setState(() {
      _uploadedFiles.add(file);
    });
  }

  @override
  Widget build(BuildContext context) {
    final messagesAsync = ref.watch(messagesProvider);
    final messageService = ref.read(messageServiceProvider);

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.serviceType),
      ),
      body: Column(
        children: [
          Expanded(
            child: messagesAsync.when(
              data: (messages) {
                final sortedMessages = List<Message>.from(messages)
                  ..sort((a, b) => a.sentAt.compareTo(b.sentAt)); // oldest → newest

                WidgetsBinding.instance.addPostFrameCallback((_) => _scrollToBottom());

                return ListView.builder(
                  controller: _scrollController,
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  itemCount: sortedMessages.length,
                  itemBuilder: (context, index) {
                    final message = sortedMessages[index];
                    return MessageBubble(
                      message: message,
                      isMine: message.senderId == messageService.userId,
                    );
                  },
                );
              },
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (err, stack) => Center(
                child: Text("Error loading messages: $err"),
              ),
            ),
          ),
          // Show uploaded files preview
          if (_uploadedFiles.isNotEmpty)
            SizedBox(
              height: 60,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: _uploadedFiles.length,
                itemBuilder: (context, index) {
                  final file = _uploadedFiles[index];
                  return Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 4),
                    child: Chip(
                      label: Text(file.name),
                      onDeleted: () {
                        setState(() {
                          _uploadedFiles.removeAt(index);
                        });
                      },
                    ),
                  );
                },
              ),
            ),
          MessageInputField(
            onSend: (text) {
              final message = Message(
                messageId: UniqueKey().toString(),
                senderId: messageService.userId ?? '',
                receiverId: '', // your backend determines the receiver
                text: text,
                type: MessageType.FARMER_AGRICULTURE,
                attachments: [],
                sentAt: DateTime.now(),
                isRead: false,
              );
              messageService.sendMessage(message, files: List<PlatformFile>.from(_uploadedFiles));
              setState(() {
                _uploadedFiles.clear();
              });
              _scrollToBottom();
            },
            onFileSend: _handleFileSend,
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }
}
