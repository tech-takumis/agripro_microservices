import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:mobile/data/services/message_service.dart';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:get/get.dart';
import '../controllers/auth_controller.dart';
import 'package:uuid/uuid.dart';

class ContactDepartmentPage extends StatefulWidget {
  const ContactDepartmentPage({super.key});

  @override
  State<ContactDepartmentPage> createState() => _ContactDepartmentPageState();
}

class _ContactDepartmentPageState extends State<ContactDepartmentPage> {
  final TextEditingController _messageController = TextEditingController();
  final AuthController authController = Get.find<AuthController>();
  final MessageService _messageService = MessageService();
  final List<Message> _messages = [];
  bool _isSending = false;

  @override
  void initState() {
    super.initState();
    _initializeMessaging();
  }

  void _initializeMessaging() async {
    // Set up message received callback
    _messageService.onMessageReceived = (message) {
      setState(() {
        _messages.add(message);
      });
    };

    // Initialize message service
    await _messageService.initializeMessageService();

    // Add welcome message using dex1dxzsignated staff userId
    final designatedStaff = _messageService.designatedStaff;
    final welcomeMessage = Message(
      id: const Uuid().v4(),
      senderId: 'SYSTEM',
      receiverId: designatedStaff?.userId ?? '',
      text: '👋 Hello! This is the Department of Agriculture support assistant.\nHow can we help you today?',
      type: MessageType.AGRICULTURE_PCIC,
      sentAt: DateTime.now(),
    );

    setState(() {
      _messages.add(welcomeMessage);
    });
  }

  void _sendMessage() async {
    final text = _messageController.text.trim();
    if (text.isEmpty) return;

    setState(() => _isSending = true);

    try {
      final designatedStaff = _messageService.designatedStaff;
      final userId = StorageService.to.getUserId();

      if (designatedStaff == null) {
        throw Exception('No designated staff available');
      }

      if (userId == null) {
        throw Exception('User ID not found');
      }

      final message = Message(
        id: const Uuid().v4(),
        senderId: userId,
        receiverId: designatedStaff.userId,
        text: text,
        type: MessageType.FARMER_AGRICULTURE,
        sentAt: DateTime.now(),
      );

      await _messageService.sendMessage(message);

      setState(() {
        _messages.add(message);
        _messageController.clear();
      });
    } catch (e) {
      Get.snackbar(
        'Error',
        'Failed to send message: ${e.toString()}',
        backgroundColor: Colors.red,
        colorText: Colors.white,
      );
    } finally {
      setState(() => _isSending = false);
    }
  }

  @override
  void dispose() {
    _messageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        title: const Text("Department of Agriculture"),
        backgroundColor: Colors.green[700],
      ),
      body: SafeArea(
        child: Column(
          children: [
            // --- Chat messages ---
            Expanded(
              child: ListView.builder(
                padding: const EdgeInsets.all(16),
                itemCount: _messages.length,
                itemBuilder: (context, index) {
                  final msg = _messages[index];
                  final userId = StorageService.to.getUserId();
                  final isUser = userId != null && msg.senderId == userId;
                  final time = DateFormat('hh:mm a').format(msg.sentAt);

                  return Align(
                    alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
                    child: Container(
                      margin: const EdgeInsets.symmetric(vertical: 6),
                      padding: const EdgeInsets.all(12),
                      constraints: BoxConstraints(
                        maxWidth: MediaQuery.of(context).size.width * 0.75
                      ),
                      decoration: BoxDecoration(
                        color: isUser ? Colors.green : Colors.white,
                        borderRadius: BorderRadius.only(
                          topLeft: const Radius.circular(14),
                          topRight: const Radius.circular(14),
                          bottomLeft: isUser
                              ? const Radius.circular(14)
                              : const Radius.circular(2),
                          bottomRight: isUser
                              ? const Radius.circular(2)
                              : const Radius.circular(14),
                        ),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.05),
                            blurRadius: 4,
                            offset: const Offset(0, 2),
                          ),
                        ],
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            msg.text,
                            style: TextStyle(
                              color: isUser ? Colors.white : Colors.black87,
                              fontSize: 14,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Align(
                            alignment: Alignment.bottomRight,
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                if (msg.attachments.isNotEmpty)
                                  const Icon(
                                    Icons.attachment,
                                    size: 12,
                                    color: Colors.grey,
                                  ),
                                const SizedBox(width: 4),
                                Text(
                                  time,
                                  style: TextStyle(
                                    fontSize: 10,
                                    color: isUser
                                        ? Colors.white70
                                        : Colors.grey.shade500,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),

            // --- Input bar ---
            Container(
              padding:
                  const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              color: Colors.white,
              child: Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: _messageController,
                      minLines: 1,
                      maxLines: 4,
                      decoration: InputDecoration(
                        hintText: "Type your message...",
                        hintStyle: const TextStyle(color: Colors.grey),
                        filled: true,
                        fillColor: Colors.grey[100],
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: 16, vertical: 10),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(30),
                          borderSide: BorderSide.none,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  _isSending
                      ? const Padding(
                          padding: EdgeInsets.all(8.0),
                          child: SizedBox(
                            width: 22,
                            height: 22,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              color: Colors.green,
                            ),
                          ),
                        )
                      : CircleAvatar(
                          radius: 24,
                          backgroundColor: Colors.green,
                          child: IconButton(
                            icon: const Icon(Icons.send_rounded,
                                color: Colors.white),
                            onPressed: _sendMessage,
                          ),
                        ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
