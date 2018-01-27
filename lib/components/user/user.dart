import 'package:firebase_database/firebase_database.dart';

class User {
  const User({this.id, this.name});

  factory User.snapshot(DataSnapshot snapshot) {
    Map<String, dynamic> value = snapshot.value;
    return new User(
      id: snapshot.key,
      name: value['name'],
    );
  }

  final String id;
  final String name;

  @override
  String toString() {
    return 'User{id: $id, name: $name}';
  }
}
