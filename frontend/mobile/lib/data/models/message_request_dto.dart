import 'dart:convert';

class MessageRequestDto {
  final String senderId;
  final String receiverId;
  final String text;
  final String type;
  final DateTime sentAt;

  MessageRequestDto({
    required this.senderId,
    required this.receiverId,
    required this.text,
    required this.type,
    required this.sentAt,
  });

  Map<String, dynamic> toJson() => {
    'senderId': senderId,
    'receiverId': receiverId,
    'text': text,
    'type': type,
    'sentAt': sentAt.toIso8601String(),
  };

  String toJsonString() => jsonEncode(toJson());
}
