import 'dart:async';

import 'package:dio/dio.dart' as dio;
import 'package:file_picker/file_picker.dart';
import 'package:get/get.dart';
import 'package:http_parser/http_parser.dart';
import 'package:mime/mime.dart';
import 'package:mobile/data/models/designated_response.dart';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/models/message_request_dto.dart';
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
    // Only process if it looks like a chat message
    if (data.containsKey('messageId') && data.containsKey('senderId') && data.containsKey('receiverId')) {
      try {
        final message = Message.fromJson(data);
        if (!_messages.any((m) => m.messageId == message.messageId)) {
          _messages.add(message);
          _controller.add([..._messages]); // ✅ notify listeners
        }
      } catch (e) {
        print('❌ [MessageService] Error parsing incoming message: $e');
      }
    } else {
      // Not a chat message, ignore or handle as needed
      print('ℹ️ [MessageService] Ignored non-chat message: $data');
    }
  }

  Future<void> sendMessage({
    required MessageRequestDto messageRequest,
    required AuthState authState,
    List<PlatformFile>? files,
  }) async {
    try {
      if (_receiverId == null) throw Exception('No designated staff to send message to');

      List<dio.MultipartFile> dioFiles = [];
      if (files != null && files.isNotEmpty) {
        for (PlatformFile platformFile in files) {
          if (platformFile.path != null) {
            final mimeType = lookupMimeType(platformFile.path!);
            dioFiles.add(await dio.MultipartFile.fromFile(
              platformFile.path!,
              filename: platformFile.name,
              contentType: mimeType != null ? MediaType.parse(mimeType) : null,
            ));
          } else {
            print('❌ [MessageService] File path is null for file: \\${platformFile.name}');
          }
        }
      }

      final createdMessage = await _messageApi.createMessage(
        messageRequest: messageRequest,
        attachments: dioFiles,
      );

      if(!_messages.any((m) => m.messageId == createdMessage.messageId)){
        _messages.add(createdMessage);
        _controller.add([..._messages]);
      }
      print('📤 [MessageService] Sent message via API: \\${createdMessage.text}');
    } catch (e) {
      print('❌ [MessageService] Error sending message via API: $e');
    }
  }

  @override
  void onClose() {
    _ws.removeListener(_handleIncomingMessage);
    _controller.close();
    super.onClose();
  }
}
