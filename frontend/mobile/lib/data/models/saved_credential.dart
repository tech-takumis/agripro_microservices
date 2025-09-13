import 'package:hive/hive.dart';

part 'saved_credential.g.dart';

@HiveType(typeId: 0)
class SavedCredential extends HiveObject {
  @HiveField(0)
  String username;

  @HiveField(1)
  String password;

  @HiveField(2)
  DateTime savedAt;

  SavedCredential({
    required this.username,
    required this.password,
    required this.savedAt,
  });

  @override
  String toString() {
    return 'SavedCredential(username: $username, savedAt: $savedAt)';
  }
}
