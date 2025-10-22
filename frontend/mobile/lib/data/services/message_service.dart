import 'dart:async';
import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:get/get.dart';
import 'package:mobile/data/models/attachment.dart';
import 'package:mobile/data/models/designated_response.dart';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/services/document_service.dart';
import 'package:mobile/data/services/message_api.dart';
import 'package:mobile/data/services/websocket.dart';
import 'package:mobile/presentation/controllers/auth_controller.dart';

import '../../injection_container.dart';

class MessageService extends GetxService {
  static MessageService get to => getIt<MessageService>();

  final _messages = <Message>[].obs;
  final _controller = StreamController<List<Message>>.broadcast();
  final WebSocketService _ws = getIt<WebSocketService>();
  final MessageApi _messageApi = MessageApi();
  final DocumentService _documentService = getIt<DocumentService>();

  String? _userId;
  String? _receiverId;
  DesignatedResponse? _designatedStaff;
  bool _initialized = false;

  String? get userId => _userId;
  String? get receiverId => _receiverId;

  List<Message> get messages => _messages;
  Stream<List<Message>> get messagesStream => _controller.stream;

  Future<MessageService> init({
    required String token,
    required String userId,
  }) async {
    if (_initialized && _userId == userId) return this;
    _initialized = true;
    _userId = userId;

    print('MessageService: Initializing with userId=$_userId');
    try {
      _designatedStaff = await _messageApi.findAgricultureDesignatedStaff();
      _receiverId = _designatedStaff?.userId;
    } catch (e) {
      print('MessageService: Error finding designated staff: $e');
      _receiverId = null;
      // Optionally, you can notify the user or UI here if needed
    }

    if (_receiverId != null && _userId!.isNotEmpty) {
      await loadMessages();
      _ws.addListener(_handleIncomingMessage);
    }

    return this;
  }

  Future<void> loadMessages() async {
    if (_userId == null || _receiverId == null) return;
    try {
      final messages = await _messageApi.getMessagesWithAgricultureStaff(_userId!);
      _messages.assignAll(messages);
      _controller.add([..._messages]); // ✅ push to stream
      print('MessageService: Loaded ${messages.length} messages');
    } catch (e) {
      print('MessageService: Error loading messages - $e');
    }
  }

  void _handleIncomingMessage(Map<String, dynamic> data) {
    try {
      final message = Message.fromJson(data);
      if (!_messages.any((m) => m.messageId == message.messageId)) {
        _messages.add(message);
        _controller.add([..._messages]); // ✅ notify listeners
      } else {
      }
    } catch (e) {
      print('❌ [MessageService] Error parsing incoming message: $e');
    }
  }

  Future<void> sendMessage(Message message, {required AuthState authState, List<PlatformFile>? files}) async {
    try {
      if (_receiverId == null) throw Exception('No designated staff to send message to');

      List<Attachment> attachmentUploaded = [];
      if (files != null && files.isNotEmpty) {
        for (PlatformFile platformFile in files) {
          if (platformFile.path != null) {
            final file = File(platformFile.path!);
            final docResponse = await _documentService.uploadDocument(
              authState: authState,
              file: file,
            );

            final attachment = Attachment(
              documentId: docResponse.documentId,
              url: docResponse.preview, // Use 'preview' as per DocumentResponse
            );

            attachmentUploaded.add(attachment);
          } else {
            print('❌ [MessageService] File path is null for file: ${platformFile.name}');
          }
        }
      }

      final messageRequest = {
        'senderId': message.senderId,
        'receiverId': _receiverId,
        'text': message.text,
        'type': message.type.toString().split('.').last,
        'attachments': attachmentUploaded,
        'sentAt': message.sentAt.toUtc().toIso8601String(),
      };

      print("[Message Service] Sending message request: $messageRequest");
      print('📤 [MessageService] Sending message: ${message.text}');
      _ws.sendMessage('/app/private.chat', messageRequest);
      print("[MessageService] After send, current messages count: \\${_messages.length}");

    } catch (e) {
      print('❌ [MessageService] Error sending message: $e');
    }
  }

  @override
  void onClose() {
    _ws.removeListener(_handleIncomingMessage);
    _controller.close();
    super.onClose();
  }
}
