import 'package:firebase_database/firebase_database.dart';

class Database {
  static final reference = FirebaseDatabase.instance.reference();
  static final instancesRef = reference.child('instances');
}
