import 'dart:math';

import 'package:boozle/components/instance/instance.dart';
import 'package:boozle/components/player/player_list.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/firebase_animated_list.dart';
import 'package:flutter/material.dart';

class PlayersComponent extends StatefulWidget {
  PlayersComponent(this.instanceHash);

  final Random rnd = new Random();
  final String instanceHash;
  final PlayerList players = new PlayerList();

  @override
  State<StatefulWidget> createState() => new PlayersComponentState();
}

class PlayersComponentState extends State<PlayersComponent> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: new FirebaseAnimatedList(
        query: Instance.reference(hash: widget.instanceHash).child('users'),
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
