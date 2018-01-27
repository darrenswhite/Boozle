import 'dart:async';

import 'package:boozle/config/database.dart';
import 'package:boozle/config/router.dart';
import 'package:boozle/util/hashids.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:logging/logging.dart';

const String kHashAlphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';
const int kMinHashLength = 4;

final Hashids _hash = new Hashids(
  alphabet: kHashAlphabet,
  minHashLength: kMinHashLength,
);

class Instance {
  Instance({
    this.index,
    this.hash,
    this.users,
  })
      : ref = reference(index: index);

  factory Instance.snapshot(DataSnapshot snapshot) {
    Map<String, dynamic> value = snapshot.value;
    return new Instance(
      index: int.parse(snapshot.key),
      hash: value[KEY_HASH],
      users: value[KEY_USERS] ?? {},
    );
  }

  static final Logger log = new Logger('Instance');

  static const String KEY_HASH = 'hash';
  static const String KEY_USER_NAME = 'name';
  static const String KEY_USERS = 'users';

  final int index;
  final String hash;
  final Map<String, dynamic> users;
  final DatabaseReference ref;

  void addUser(String uid, String name) {
    log.info('Adding user to instance: $uid ($name)');
    ref.child('$KEY_USERS/$uid').set({KEY_USER_NAME: name});
    users[uid] = true;
  }

  /// Exactly one of index or hash must be given
  static Future<Instance> fromDatabase({int index, String hash}) async {
    return new Instance.snapshot(
        await reference(index: index, hash: hash).once());
  }

  static DatabaseReference reference({int index, String hash}) {
    assert((index != null && hash == null) || (index == null && hash != null));
    if (hash != null) {
      index = _hash.decode(hash)[0];
    }
    return Database.instancesRef.child(index.toString());
  }

  /// Removes a user. If removing the last user will show an alert dialog
  /// for confirmation. Removing the last user will remove the instance.
  Future<bool> removeUser(BuildContext context, String uid) async {
    if (users.length > 1 || await _showRemoveUserDialog(context)) {
      log.info('Removing user from instance: $uid');
      ref.child('$KEY_USERS/$uid').set(null);
      users.remove(uid);
      if (users.isEmpty) {
        ref.set(null);
      }
      return true;
    } else {
      return false;
    }
  }

  Future<bool> _showRemoveUserDialog(BuildContext context) {
    log.info('Showing remove user alert dialog');
    return showDialog<bool>(
          context: context,
          child: new AlertDialog(
            title: const Text('Are you sure you want to leave?'),
            content: const Text('You are the only user in this instance. '
                'Leaving will remove the instance. '
                'Really leave this instance?'),
            actions: <Widget>[
              new FlatButton(
                child: const Text('YES'),
                onPressed: () => Navigator.of(context).pop(true),
              ),
              new FlatButton(
                child: const Text('NO'),
                onPressed: () => Navigator.of(context).pop(false),
              ),
            ],
          ),
        ) ??
        false;
  }

  static Future<Instance> transaction() async {
    String hash;
    int index;
    final TransactionResult result =
        await Database.instancesRef.runTransaction((data) async {
      final List<dynamic> instances = new List.from(data.value ?? []);
      index = instances.indexOf(null);

      log.fine('Total number of instances: ${instances.length}');

      if (index == -1) {
        index = instances.length;
      } else {
        instances.removeAt(index);
      }

      log.fine('New instance index: $index');

      hash = _hash.encode([index]);

      log.fine('New instance hash: $hash');

      instances.insert(index, {'hash': hash});

      data.value = instances;

      return data;
    });

    if (result.committed) {
      log.info('New instance successfully created: $hash ($index)');
      return fromDatabase(index: index);
    } else {
      log.warning('Failed to create new instance: $hash ($index)');
      if (result.error != null) {
        log.severe(result.error.message);
      }
      return null;
    }
  }

  void view(BuildContext context) {
    log.info('Navigiating to tabs with instance hash: $hash');
    Navigator.pushNamed(context, Router.TABS + hash);
  }
}
