import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

class WebSocketService {
  StompClient? _client;

  void connect({
    required String url,
    required String accessToken,
    void Function(StompFrame frame)? onConnect,
    void Function(dynamic error)? onError,
  }) {
    _client = StompClient(
      config: StompConfig.SockJS(
        url: url,
        onConnect: onConnect,
        beforeConnect: () async {
          // Optionally add delay or other logic
        },
        onWebSocketError: onError,
        stompConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
        webSocketConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
        // Uncomment for debugging
        // onDebugMessage: (msg) => print('STOMP DEBUG: $msg'),
      ),
    );
    _client?.activate();
  }

  void subscribe({
    required String destination,
    required void Function(StompFrame frame) onMessage,
  }) {
    _client?.subscribe(
      destination: destination,
      callback: onMessage,
    );
  }

  void send({
    required String destination,
    required String body,
    Map<String, String>? headers,
  }) {
    _client?.send(
      destination: destination,
      body: body,
      headers: headers,
    );
  }

  void disconnect() {
    _client?.deactivate();
  }

  bool get isConnected => _client?.connected ?? false;
}

