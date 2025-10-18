import 'dart:async';
import 'package:get/get.dart';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/models/designated_response.dart';
import 'package:mobile/data/services/websocket.dart';
import 'package:mobile/data/services/message_api.dart';
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

  Future<void> sendMessage(Message message) async {
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

      print('📤 [MessageService] Sending message: ${message.text}');
      _ws.sendMessage('/app/private.chat', messageRequest);

      _messages.add(message);
      _controller.add([..._messages]); // ✅ push local update
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
