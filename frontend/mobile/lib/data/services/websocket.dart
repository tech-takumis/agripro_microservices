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

    // Subscribe to private queue
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
            'type': message['type'] ?? 'FARMER_AGRICULTURE',
            'attachments': message['attachments'] ?? [],
            'sentAt': message['timestamp'] ?? message['sentAt'],
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

    print('✅ [WebSocket] Subscribed to /user/queue/private.messages');
  }

  void sendMessage(String destination, Map<String, dynamic> body) {
    if (!_isConnected || _client == null) {
      print('⚠️ [WebSocket] Not connected');
      return;
    }

    final token = getIt<StorageService>().getWebSocketToken();
    _client!.send(
      destination: destination,
      body: jsonEncode(body),
      headers: {
        'content-type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );
    print('📤 [WebSocket] Sent to $destination');
  }

  void addListener(MessageCallback callback) {
    if (!_subscribers.contains(callback)) _subscribers.add(callback);
  }

  void removeListener(MessageCallback callback) {
    _subscribers.remove(callback);
  }

  void disconnect() {
    if (_client != null) {
      print('🔌 [WebSocket] Disconnecting...');
      _client?.deactivate();
      _client = null;
      _isConnected = false;
      _isConnecting = false;
      _subscribers.clear();
    } else {
      print('✅ [WebSocket] Already disconnected');
    }
  }

  void _handleConnectionError() {
    _isConnected = false;
    _isConnecting = false;
  }
}
