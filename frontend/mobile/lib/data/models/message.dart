import 'attachment.dart';

// Message type enum to match backend types
enum MessageType {
  FARMER_AGRICULTURE,
  AGRICULTURE_PCIC,
}

class Message {
  final String messageId;
  final String senderId;
  final String receiverId;
  final String text;
  final MessageType type;
  final List<Attachment> attachments;
  final DateTime sentAt;
  final bool isRead;

  Message({
    required this.messageId,
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
      messageId: json['messageId'],
      senderId: json['senderId'],
      receiverId: json['receiverId'],
      text: json['text'],
      type: MessageType.values.firstWhere(
        (e) => e.toString().split('.').last == json['type'],
        orElse: () => MessageType.FARMER_AGRICULTURE,
      ),
      attachments: (json['attachments'] as List?)
          ?.map((attachment) => Attachment.fromJson(attachment))
          .toList() ?? [],
      sentAt: DateTime.parse(json['sentAt']),
      isRead: json['isRead'] ?? false,
    );
  }

  // Convert to JSON
  Map<String, dynamic> toJson() {
    return {
      'messageId': messageId,
      'senderId': senderId,
      'receiverId': receiverId,
      'text': text,
      'type': type.toString().split('.').last,
      'attachments': attachments.map((attachment) => attachment.toJson()).toList(),
      'sentAt': sentAt.toIso8601String(),
    };
  }

  // Create a copy of the message with updated fields
  Message copyWith({
    String? messageId,
    String? senderId,
    String? receiverId,
    String? text,
    MessageType? type,
    List<Attachment>? attachments,
    DateTime? sentAt,
    bool? isRead,
  }) {
    return Message(
      messageId: messageId ?? this.messageId,
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
