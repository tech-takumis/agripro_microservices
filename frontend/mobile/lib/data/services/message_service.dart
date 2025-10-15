import 'dart:convert';
import 'package:mobile/data/models/message.dart';
import 'package:mobile/data/models/designated_response.dart';
import 'package:mobile/data/services/websocket.dart';
import 'package:mobile/data/services/storage_service.dart';
import 'package:mobile/data/services/message_api.dart';

class MessageService {
  static final MessageService _instance = MessageService._internal();
  factory MessageService() => _instance;
  MessageService._internal();

  final WebSocketService _webSocketService = WebSocketService();
  final MessageApi _messageApi = MessageApi();
  final List<Message> _messages = [];
  Function(Message)? onMessageReceived;
  DesignatedResponse? _designatedStaff;

  // Initialize WebSocket connection and load messages
  Future<void> initializeMessageService() async {
    try {
      // First, get the designated staff
      _designatedStaff = await _messageApi.findAgricultureDesignatedStaff();
      print('Found designated staff: ${_designatedStaff?.userId}');

      const wsUrl = 'http://localhost:9040/ws';

      // Connect to WebSocket with authentication from storage
      _webSocketService.connect(
        url: wsUrl,
        onConnect: (frame) {
          print('MessageService: WebSocket connected');
          _subscribeToMessageTopics();
        },
        onError: (error) {
          print('MessageService: WebSocket error - $error');
        },
      );

      // Load existing messages if we have a designated staff
      if (_designatedStaff != null) {
        final messages = await loadExistingMessages();
        _messages.addAll(messages);
      }
    } catch (e) {
      print('MessageService: Initialization error - $e');
      rethrow;
    }
  }

  // Load existing messages from the server
  Future<List<Message>> loadExistingMessages() async {
    try {
      final farmerId = StorageService.to.getUserId();
      if (farmerId == null) throw Exception('User ID not found');

      return await _messageApi.getMessagesWithAgricultureStaff(farmerId);
    } catch (e) {
      print('MessageService: Error loading messages - $e');
      rethrow;
    }
  }

  // Subscribe to relevant message topics
  void _subscribeToMessageTopics() {
    final userId = StorageService.to.getUserId();
    if (userId == null) {
      print('MessageService: Cannot subscribe - user ID not found');
      return;
    }

    // Subscribe to private messages for this user
    // This matches Spring Boot's /user/{userId}/topic/private pattern
    _webSocketService.subscribeToPrivateMessages(
      userId: userId,
      onMessage: _handleIncomingMessage,
    );

    // Subscribe to queue messages (if needed)
    _webSocketService.subscribe(
      destination: '/user/queue/messages',
      onMessage: _handleIncomingMessage,
    );

    // Subscribe to broadcasts (if needed)
    _webSocketService.subscribe(
      destination: '/topic/broadcasts',
      onMessage: _handleIncomingMessage,
    );

    print('MessageService: Subscribed to all message topics');
  }

  // Handle incoming WebSocket messages
  void _handleIncomingMessage(dynamic frame) {
    try {
      final messageData = json.decode(frame.body);
      final message = Message.fromJson(messageData);
      _messages.add(message);
      onMessageReceived?.call(message);
      print('MessageService: Received message - ${message.text}');
    } catch (e) {
      print('MessageService: Error handling message - $e');
    }
  }

  Future<void> sendMessage(Message message) async {
    try {
      if (_designatedStaff == null) {
        throw Exception('No designated staff to send message to');
      }

      // Use the sendPrivateMessage method that sends to /app/private.chat
      _webSocketService.sendPrivateMessage(
        receiverId: message.receiverId,
        text: message.text,
        messageId: message.id,
        additionalData: {
          'type': message.type.toString().split('.').last,
          if (message.attachments.isNotEmpty)
            'attachments': message.attachments,
        },
      );

      print('MessageService: Message sent successfully');
    } catch (e) {
      print('MessageService: Error sending message - $e');
      rethrow;
    }
  }

  // Get the designated staff information
  DesignatedResponse? get designatedStaff => _designatedStaff;

  // Get message history
  List<Message> getMessageHistory() {
    return List.from(_messages)..sort((a, b) => a.sentAt.compareTo(b.sentAt));
  }

  // Disconnect WebSocket
  void disconnect() {
    _webSocketService.disconnect();
    _messages.clear();
    _designatedStaff = null;
  }

  // Get unread messages count
  int getUnreadMessagesCount() {
    return _messages.where((msg) => !msg.isRead).length;
  }
}
