// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'saved_credential.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class SavedCredentialAdapter extends TypeAdapter<SavedCredential> {
  @override
  final int typeId = 0;

  @override
  SavedCredential read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return SavedCredential(
      username: fields[0] as String,
      password: fields[1] as String,
      savedAt: fields[2] as DateTime,
    );
  }

  @override
  void write(BinaryWriter writer, SavedCredential obj) {
    writer
      ..writeByte(3)
      ..writeByte(0)
      ..write(obj.username)
      ..writeByte(1)
      ..write(obj.password)
      ..writeByte(2)
      ..write(obj.savedAt);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is SavedCredentialAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
