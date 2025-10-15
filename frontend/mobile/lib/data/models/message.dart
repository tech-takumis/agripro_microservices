// Message type enum to match backend types
enum MessageType {
  FARMER_AGRICULTURE,
  AGRICULTURE_PCIC,
}

class Message {
  final String id; // UUID from backend
  final String senderId; // UUID
  final String receiverId; // UUID
  final String text;
  final MessageType type;
  final List<String> attachments; // List of attachment UUIDs
  final DateTime sentAt;
  final bool isRead;

  Message({
    required this.id,
    required this.senderId,
    required this.receiverId,
    required this.text,
    required this.type,
    this.attachments = const [],
    required this.sentAt,
    this.isRead = false,
  });

  // Create from JSON
  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      id: json['id'],
      senderId: json['senderId'],
      receiverId: json['receiverId'],
      text: json['text'],
      type: MessageType.values.firstWhere(
        (e) => e.toString().split('.').last == json['type'],
        orElse: () => MessageType.FARMER_AGRICULTURE,
      ),
      attachments: List<String>.from(json['attachments'] ?? []),
      sentAt: DateTime.parse(json['sentAt']),
      isRead: json['isRead'] ?? false,
    );
  }

  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'senderId': senderId,
      'receiverId': receiverId,
      'text': text,
      'type': type.toString().split('.').last,
      'attachments': attachments,
      'sentAt': sentAt.toIso8601String(),
    };
  }

  // Create a copy of the message with updated fields
  Message copyWith({
    String? id,
    String? senderId,
    String? receiverId,
    String? text,
    MessageType? type,
    List<String>? attachments,
    DateTime? sentAt,
    bool? isRead,
  }) {
    return Message(
      id: id ?? this.id,
      senderId: senderId ?? this.senderId,
      receiverId: receiverId ?? this.receiverId,
      text: text ?? this.text,
      type: type ?? this.type,
      attachments: attachments ?? this.attachments,
      sentAt: sentAt ?? this.sentAt,
      isRead: isRead ?? this.isRead,
    );
  }
}
