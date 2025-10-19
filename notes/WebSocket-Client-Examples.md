# WebSocket Client Configuration Examples

## Vue.js Client (using @stomp/stompjs)

```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Initialize STOMP client
const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:9040/ws'),
  
  connectHeaders: {
    // Authentication token will be sent via cookie (ACCESS_TOKEN)
    // or can be sent in headers if needed
  },
  
  onConnect: (frame) => {
    console.log('Connected to WebSocket', frame);
    
    // Subscribe to private messages using /user/queue/private
    client.subscribe('/user/queue/private', (message) => {
      const receivedMessage = JSON.parse(message.body);
      console.log('Received private message:', receivedMessage);
      
      // Handle the received message in your Vue component
      // For example, add it to your messages array
      // messages.value.push(receivedMessage);
    });
    
    // Subscribe to conversation topics if needed
    client.subscribe('/topic/conversation/' + conversationId, (message) => {
      const receivedMessage = JSON.parse(message.body);
      console.log('Received conversation message:', receivedMessage);
    });
  },
  
  onStompError: (frame) => {
    console.error('STOMP error', frame);
  },
  
  debug: (str) => {
    console.log('STOMP debug:', str);
  }
});

// Activate the client
client.activate();

// Send a private message
function sendPrivateMessage(receiverId, text, attachments = []) {
  const messageDto = {
    receiverId: receiverId,
    text: text,
    attachments: attachments,
    timestamp: new Date().toISOString()
  };
  
  client.publish({
    destination: '/app/private.chat',
    body: JSON.stringify(messageDto)
  });
}

// Send a conversation message
function sendConversationMessage(conversationId, text) {
  const messageDto = {
    conversationId: conversationId,
    text: text,
    timestamp: new Date().toISOString()
  };
  
  client.publish({
    destination: '/app/chat.send',
    body: JSON.stringify(messageDto)
  });
}

// Disconnect when component unmounts
function disconnect() {
  if (client.active) {
    client.deactivate();
  }
}
```

## Flutter Client (using stomp_dart_client)

```dart
import 'package:stomp_dart_client/stomp_dart_client.dart';
import 'dart:convert';

class WebSocketService {
  late StompClient stompClient;
  Function(Map<String, dynamic>)? onMessageReceived;
  
  void connect(String accessToken) {
    stompClient = StompClient(
      config: StompConfig(
        url: 'http://localhost:9040/ws',
        
        // SockJS connection
        webSocketConnectHeaders: {
          'Cookie': 'ACCESS_TOKEN=$accessToken',
        },
        
        onConnect: (StompFrame frame) {
          print('Connected to WebSocket');
          
          // Subscribe to private messages using /user/queue/private
          stompClient.subscribe(
            destination: '/user/queue/private',
            callback: (StompFrame frame) {
              if (frame.body != null) {
                final message = jsonDecode(frame.body!);
                print('Received private message: $message');
                
                // Notify listeners
                if (onMessageReceived != null) {
                  onMessageReceived!(message);
                }
              }
            },
          );
          
          // Subscribe to conversation topics if needed
          // stompClient.subscribe(
          //   destination: '/topic/conversation/$conversationId',
          //   callback: (StompFrame frame) {
          //     if (frame.body != null) {
          //       final message = jsonDecode(frame.body!);
          //       print('Received conversation message: $message');
          //     }
          //   },
          // );
        },
        
        onWebSocketError: (dynamic error) {
          print('WebSocket error: $error');
        },
        
        onStompError: (StompFrame frame) {
          print('STOMP error: ${frame.body}');
        },
        
        onDisconnect: (StompFrame frame) {
          print('Disconnected from WebSocket');
        },
      ),
    );
    
    stompClient.activate();
  }
  
  void sendPrivateMessage({
    required String receiverId,
    required String text,
    List<Map<String, dynamic>>? attachments,
  }) {
    final messageDto = {
      'receiverId': receiverId,
      'text': text,
      'attachments': attachments ?? [],
      'timestamp': DateTime.now().toIso8601String(),
    };
    
    stompClient.send(
      destination: '/app/private.chat',
      body: jsonEncode(messageDto),
    );
  }
  
  void sendConversationMessage({
    required String conversationId,
    required String text,
  }) {
    final messageDto = {
      'conversationId': conversationId,
      'text': text,
      'timestamp': DateTime.now().toIso8601String(),
    };
    
    stompClient.send(
      destination: '/app/chat.send',
      body: jsonEncode(messageDto),
    );
  }
  
  void disconnect() {
    if (stompClient.connected) {
      stompClient.deactivate();
    }
  }
}
```

## Key Points

1. **Backend Configuration:**
   - Uses `convertAndSendToUser(receiverId, "/queue/private", dto)`
   - Custom Principal returns `userId` instead of `username`
   - User destination resolver uses the `userId` from CustomUserDetails

2. **Client Subscription:**
   - Both Vue and Flutter subscribe to `/user/queue/private`
   - Spring automatically resolves this to `/user/{userId}/queue/private`
   - No need to specify the userId in the subscription path

3. **Sending Messages:**
   - Send to `/app/private.chat` for private messages
   - Send to `/app/chat.send` for conversation messages
   - Backend extracts sender's userId from the authenticated Principal

4. **Authentication:**
   - Vue: Sends token via cookie (ACCESS_TOKEN) automatically
   - Flutter: Explicitly includes token in connection headers or cookies
   - Backend validates JWT and extracts userId for destination routing

## Testing

1. Connect both Vue and Flutter clients
2. Send a message from Vue to Flutter user (or vice versa)
3. Check logs to verify:
   - Authentication shows correct userId
   - Message routing uses userId for destination
   - Message is received by the target client

## Troubleshooting

If messages still don't arrive:
- Check that both clients are connected (check server logs for "Successfully authenticated user")
- Verify the receiverId in the message matches the target user's userId (UUID format)
- Check browser/Flutter console for subscription confirmations
- Enable debug logging to see message routing

