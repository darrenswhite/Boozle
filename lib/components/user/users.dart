import 'dart:math';

import 'package:boozle/components/instance/instance.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/firebase_animated_list.dart';
import 'package:flutter/material.dart';

class UsersComponent extends StatelessWidget {
  UsersComponent(this.instanceHash);

  final Random rnd = new Random();
  final String instanceHash;

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: new FirebaseAnimatedList(
        query: Instance.reference(hash: instanceHash).child(Instance.KEY_USERS),
        itemBuilder: (BuildContext context, DataSnapshot snapshot,
            Animation<double> animation, int index) {
          return new SizeTransition(
            sizeFactor: animation,
            child: new Card(
              child: new ListTile(
                title: new Text(snapshot.key.toString()),
              ),
            ),
          );
        },
      ),
    );
  }
}
