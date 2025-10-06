import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class ContactDepartmentPage extends StatefulWidget {
  const ContactDepartmentPage({super.key});

  @override
  State<ContactDepartmentPage> createState() => _ContactDepartmentPageState();
}

class _ContactDepartmentPageState extends State<ContactDepartmentPage> {
  final TextEditingController _messageController = TextEditingController();

  // Simulated chat messages
  final List<Map<String, dynamic>> _messages = [
    {
      'sender': 'bot',
      'text':
          '👋 Hello! This is the Department of Agriculture support assistant.\nHow can we help you today?',
      'time': DateTime.now(),
    },
  ];

  bool _isSending = false;

  void _sendMessage() async {
    final text = _messageController.text.trim();
    if (text.isEmpty) return;

    setState(() {
      _messages.add({
        'sender': 'user',
        'text': text,
        'time': DateTime.now(),
      });
      _messageController.clear();
      _isSending = true;
    });

    // Simulate bot response delay
    await Future.delayed(const Duration(seconds: 1));

    setState(() {
      _messages.add({
        'sender': 'bot',
        'text': _generateBotResponse(text),
        'time': DateTime.now(),
      });
      _isSending = false;
    });
  }

  String _generateBotResponse(String userMessage) {
    userMessage = userMessage.toLowerCase();

    if (userMessage.contains('hello') || userMessage.contains('hi')) {
      return 'Hello! 👋 How can we assist you regarding your agricultural concerns today?';
    } else if (userMessage.contains('insurance')) {
      return 'For crop insurance inquiries, please make sure your latest application is submitted. Would you like to track its status? 🌾';
    } else if (userMessage.contains('contact')) {
      return '📞 You can reach our Municipal Agriculture Office directly at (02) 1234-5678 or via email: support@agriculture.gov.ph';
    } else if (userMessage.contains('thank')) {
      return 'You’re welcome! 😊 We’re glad to help.';
    } else {
      return 'Got it! Our support team will review your message soon. Please wait for a follow-up. ✅';
    }
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
                  final isUser = msg['sender'] == 'user';
                  final time =
                      DateFormat('hh:mm a').format(msg['time'] as DateTime);

                  return Align(
                    alignment:
                        isUser ? Alignment.centerRight : Alignment.centerLeft,
                    child: Container(
                      margin: const EdgeInsets.symmetric(vertical: 6),
                      padding: const EdgeInsets.all(12),
                      constraints: BoxConstraints(
                          maxWidth: MediaQuery.of(context).size.width * 0.75),
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
                            msg['text'],
                            style: TextStyle(
                              color: isUser ? Colors.white : Colors.black87,
                              fontSize: 14,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Align(
                            alignment: Alignment.bottomRight,
                            child: Text(
                              time,
                              style: TextStyle(
                                fontSize: 10,
                                color: isUser
                                    ? Colors.white70
                                    : Colors.grey.shade500,
                              ),
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
