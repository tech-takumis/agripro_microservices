import 'dart:async';
import 'package:get/get.dart';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/models/designated_response.dart';
import 'package:mobile/data/services/websocket.dart';
import 'package:mobile/data/services/message_api.dart';
import 'package:file_picker/file_picker.dart';
import '../../injection_container.dart';

class MessageService extends GetxService {
  static MessageService get to => getIt<MessageService>();

  final _messages = <Message>[].obs;
  final _controller = StreamController<List<Message>>.broadcast();
  final WebSocketService _ws = getIt<WebSocketService>();
  final MessageApi _messageApi = MessageApi();

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
    _designatedStaff = await _messageApi.findAgricultureDesignatedStaff();
    _receiverId = _designatedStaff?.userId;

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
      print('💬 [MessageService] Incoming message: ${message.text}');
      if (!_messages.any((m) => m.messageId == message.messageId)) {
        _messages.add(message);
        _controller.add([..._messages]); // ✅ notify listeners
      }
    } catch (e) {
      print('❌ [MessageService] Error parsing incoming message: $e');
    }
  }

  Future<void> sendMessage(Message message, {List<PlatformFile>? files}) async {
    try {
      if (_receiverId == null) throw Exception('No designated staff to send message to');

      final messageRequest = {
        'senderId': message.senderId,
        'receiverId': _receiverId,
        'text': message.text,
        'type': message.type.toString().split('.').last,
        'attachments': [],
        'sentAt': message.sentAt.toUtc().toIso8601String(),
      };

      // Handle file attachments (if any)
      // Note: Need to implement actual file upload to my backend using
      // the /api/document api this well return
      /*
        {
           "documentId": "445c43a9-dd4b-4a7a-a0d1-35502f0ac748",
            "uploadedBy": "09ae13c1-e44d-478f-b58c-90f1e1cfa2dc",
            "fileName": "6e99b2d7-d880-4db0-9532-81a0752706e7.jpg",
            "fileType": "image/jpeg",
            "fileSize": null,
            "objectKey": "459e69d7-dea6-457f-95cf-b19b719af3eb.jpg",
            "uploadedAt": "2025-10-18T21:46:21.079470",
            "preview": "http://localhost:9000/documents/459e69d7-dea6-457f-95cf-b19b719af3eb.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio%2F20251018%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251018T134621Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=6e24567c8b29093b398253cae2df2b92fb2ebb27f8ab47b58ce38095676ab3d3"
      }

      And create an attachment like this:
      {
        "documentId": "445c43a9-dd4b-4a7a-a0d1-35502f0ac748",
        "url": "http://localhost:9000/documents/459e69d7-dea6-457f-95cf-b19b719af3eb.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio%2F20251018%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20251018T134621Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=6e24567c8b29093b398253cae2df2b92fb2ebb27f8ab47b58ce38095676ab3d3"

      }

      If user uploade more than one attachment, list them all here.
       Add this to the attachments list in the messageRequest
       */
      if (files != null && files.isNotEmpty) {
        // You may want to upload files and add their URLs to attachments here
        // For now, just add file names as a placeholder
        messageRequest['attachments'] = files.map((f) => {'name': f.name}).toList();
      }

      print('📤 [MessageService] Sending message: ${message.text}');
      _ws.sendMessage('/app/private.chat', messageRequest);

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
