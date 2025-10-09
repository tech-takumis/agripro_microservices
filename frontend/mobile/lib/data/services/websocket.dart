import 'package:stomp_dart_client/stomp_dart_client.dart';

class WebSocketService {
  StompClient? _client;

  void connect({
    required String url,
    required String accessToken,
    required StompFrameCallback onConnect,
    required StompWebSocketErrorCallback onError,
  }) {
    _client = StompClient(
      config: StompConfig.sockJS(
        url: url,
        onConnect: onConnect,
        beforeConnect: () async {},
        onWebSocketError: onError,
        stompConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
        webSocketConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
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

