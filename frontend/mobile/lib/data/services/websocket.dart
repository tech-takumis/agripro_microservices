import 'dart:convert';
import 'package:stomp_dart_client/stomp_dart_client.dart';
import 'package:mobile/data/services/storage_service.dart';
import '../../injection_container.dart';

typedef MessageCallback = void Function(Map<String, dynamic> message);

class WebSocketService {
  StompClient? _client;
  bool _isConnecting = false;
  bool _isConnected = false;
  final List<MessageCallback> _subscribers = [];

  bool get isConnected => _isConnected;

  Future<void> connect({String url = 'ws://localhost:9001/ws'}) async {
    if (_isConnected || _isConnecting) {
      print('🔄 [WebSocket] Already connected or connecting...');
      return;
    }

    _isConnecting = true;
    final token = getIt<StorageService>().getWebSocketToken();
    if (token == null) {
      print('❌ [WebSocket] No access token found');
      _isConnecting = false;
      return;
    }

    print('🌐 [WebSocket] Connecting to $url...');

    _client = StompClient(
      config: StompConfig(
        url: url,
        onConnect: _onConnect,
        onWebSocketError: (error) {
          print('❌ [WebSocket] Error: $error');
          _handleConnectionError();
        },
        stompConnectHeaders: {'Authorization': 'Bearer $token'},
        webSocketConnectHeaders: {'Authorization': 'Bearer $token'},
        heartbeatIncoming: const Duration(milliseconds: 4000),
        heartbeatOutgoing: const Duration(milliseconds: 4000),
        reconnectDelay: const Duration(milliseconds: 5000),
      ),
    );

    try {
      _client?.activate();
    } catch (e) {
      print('❌ [WebSocket] Failed to activate: $e');
      _handleConnectionError();
    }
  }

  void _onConnect(StompFrame frame) {
    print('✅ [WebSocket] Connected');
    _isConnected = true;
    _isConnecting = false;

    _client?.subscribe(
      destination: '/user/queue/private.messages',
      callback: (frame) {
        try {
          final message = jsonDecode(frame.body!);
          print('📩 [WebSocket] Message: $message');
          // Normalize backend message keys to match frontend model
          final normalized = {
            'messageId': message['id'] ?? message['messageId'],
            'senderId': message['senderId'],
            'receiverId': message['receiverId'],
            'text': message['text'],
            'type': message['type'] ?? message['conversationType'] ?? 'FARMER_AGRICULTURE',
            'attachments': message['attachments'] ?? message['attachmentResponses'] ?? [],
            'sentAt': message['sentAt'] ?? message['timestamp'],
            'isRead': message['isRead'] ?? false,
          };
          for (final sub in _subscribers) {
            sub(normalized);
          }
        } catch (e) {
          print('❌ [WebSocket] Parse error: $e');
        }
      },
    );

    _client?.subscribe(
        destination: '/user/queue/application.notifications',
        callback: (frame){
          try {
            final notification = jsonDecode(frame.body!);
            print('🔔 [WebSocket] Notification: $notification');
            for (final sub in _subscribers) {
              sub(notification);
            }
          } catch (e) {
            print('❌ [WebSocket] Notification parse error: $e');
          }
        });

    print('✅ [WebSocket] Subscribed to /user/queue/private.messages and /user/queue/application.notifications');
  }

  void _handleConnectionError() {
    _isConnected = false;
    _isConnecting = false;
  }

  // Add a message callback subscriber
  void addListener(MessageCallback callback) {
    if (!_subscribers.contains(callback)) {
      _subscribers.add(callback);
    }
  }

  // Remove a message callback subscriber
  void removeListener(MessageCallback callback) {
    _subscribers.remove(callback);
  }

  // Disconnect and clean up the WebSocket connection
  void disconnect() {
    if (_client != null) {
      print('🔌 [WebSocket] Disconnecting...');
      _client!.deactivate();
      _client = null;
    }
    _isConnected = false;
    _isConnecting = false;
    _subscribers.clear();
  }
}
