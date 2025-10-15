import 'package:stomp_dart_client/stomp_dart_client.dart';
import 'package:mobile/data/services/storage_service.dart';

class WebSocketService {
  static final WebSocketService _instance = WebSocketService._internal();
  factory WebSocketService() => _instance;
  WebSocketService._internal();

  StompClient? _client;
  bool _isConnecting = false;
  final Map<String, dynamic> _subscriptions = {};

  void connect({
    required String url,
    required StompFrameCallback onConnect,
    required StompWebSocketErrorCallback onError,
  }) {
    if (_client?.connected ?? false) {
      print('WebSocket: Already connected, reusing existing connection');
      // Call onConnect if already connected to allow resubscription
      onConnect(StompFrame(command: 'CONNECTED'));
      return;
    }

    if (_isConnecting) {
      print('WebSocket: Connection already in progress');
      return;
    }

    // Get access token from storage
    final accessToken = StorageService.to.getToken();
    if (accessToken == null) {
      print('WebSocket: No access token available');
      onError(Exception('No access token found'));
      return;
    }

    print('WebSocket: Initializing connection to $url');
    _isConnecting = true;

    _client = StompClient(
      config: StompConfig.sockJS(
        url: url,
        onConnect: (frame) {
          print('WebSocket: Connection established successfully');
          print('WebSocket: Connected with session ${frame.headers?['session-id']}');
          _isConnecting = false;
          onConnect(frame);
        },
        beforeConnect: () async {
          print('WebSocket: Attempting to connect...');
        },
        onWebSocketError: (error) {
          print('WebSocket: Error occurred - ${error.toString()}');
          _isConnecting = false;
          onError(error);
        },
        onDisconnect: (frame) {
          print('WebSocket: Disconnected - ${frame?.body ?? 'No reason provided'}');
          _isConnecting = false;
          _subscriptions.clear();
        },
        onStompError: (frame) {
          print('WebSocket STOMP Error: ${frame.body}');
          _isConnecting = false;
        },
        onDebugMessage: (message) {
          print('WebSocket Debug: $message');
        },
        stompConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
        webSocketConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
      ),
    );

    try {
      _client?.activate();
    } catch (e) {
      print('WebSocket: Failed to activate connection - $e');
      _isConnecting = false;
      throw e;
    }
  }

  void subscribe({
    required String destination,
    required void Function(StompFrame frame) onMessage,
  }) {
    if (!(_client?.connected ?? false)) {
      print('WebSocket: Cannot subscribe - not connected');
      return;
    }

    // Unsubscribe if already subscribed to avoid duplicates
    if (_subscriptions.containsKey(destination)) {
      print('WebSocket: Already subscribed to $destination');
      return;
    }

    print('WebSocket: Subscribing to $destination');
    final subscription = _client?.subscribe(
      destination: destination,
      callback: (frame) {
        print('WebSocket: Received message on $destination');
        print('WebSocket: Message body: ${frame.body}');
        onMessage(frame);
      },
    );

    _subscriptions[destination] = subscription;
  }

  void subscribeToPrivateMessages({
    required String userId,
    required void Function(StompFrame frame) onMessage,
  }) {
    // Subscribe to private messages for this user
    // Pattern: /user/{userId}/topic/private
    final destination = '/user/$userId/topic/private';
    subscribe(destination: destination, onMessage: onMessage);
  }

  void sendPrivateMessage({
    required String receiverId,
    required String text,
    String? messageId,
    Map<String, dynamic>? additionalData,
  }) {
    if (!(_client?.connected ?? false)) {
      print('WebSocket: Cannot send message - not connected');
      throw Exception('WebSocket not connected');
    }

    final accessToken = StorageService.to.getToken();
    final senderId = StorageService.to.getUserId();

    if (senderId == null) {
      throw Exception('User ID not found');
    }

    final messagePayload = {
      'id': messageId,
      'senderId': senderId,
      'receiverId': receiverId,
      'text': text,
      'sentAt': DateTime.now().toIso8601String(),
      ...?additionalData,
    };

    print('WebSocket: Sending private message to $receiverId');
    print('WebSocket: Message payload: $messagePayload');

    _client?.send(
      destination: '/app/private.chat',
      body: messagePayload.entries
          .map((e) => '"${e.key}":${e.value is String ? '"${e.value}"' : e.value}')
          .join(','),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'content-type': 'application/json',
      },
    );
  }

  void send({
    required String destination,
    required String body,
    Map<String, String>? headers,
  }) {
    if (!(_client?.connected ?? false)) {
      print('WebSocket: Cannot send message - not connected');
      throw Exception('WebSocket not connected');
    }

    final accessToken = StorageService.to.getToken();
    final allHeaders = {
      'Authorization': 'Bearer $accessToken',
      'content-type': 'application/json',
      ...?headers,
    };

    print('WebSocket: Sending message to $destination');
    print('WebSocket: Message body: $body');

    _client?.send(
      destination: destination,
      body: body,
      headers: allHeaders,
    );
  }

  void unsubscribe(String destination) {
    if (_subscriptions.containsKey(destination)) {
      _subscriptions[destination]?.unsubscribe();
      _subscriptions.remove(destination);
      print('WebSocket: Unsubscribed from $destination');
    }
  }

  void disconnect() {
    if (_client?.connected ?? false) {
      print('WebSocket: Initiating disconnect');
      _subscriptions.clear();
      _client?.deactivate();
    } else {
      print('WebSocket: Already disconnected');
    }
  }

  bool get isConnected => _client?.connected ?? false;
}
