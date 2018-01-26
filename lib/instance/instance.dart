import 'dart:async';

import 'package:boozle/config/database.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:logging/logging.dart';

class Instance {
  const Instance({
    this.index,
    this.hash,
  });

  factory Instance.snapshot(DataSnapshot snapshot) {
    Map<String, dynamic> value = snapshot.value;
    return new Instance(
      index: int.parse(snapshot.key),
      hash: value['hash'],
    );
  }

  static Future<Instance> fromDatabase(String key) async {
    return new Instance.snapshot(await Database.instancesRef.child(key).once());
  }

  static final Logger log = new Logger('Chat');

  final int index;
  final String hash;
}
