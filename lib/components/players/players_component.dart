import 'dart:math';

import 'package:boozle/components/players/player_list.dart';
import 'package:flutter/material.dart';

class PlayersComponent extends StatefulWidget {
  final Random rnd = new Random();
  final PlayerList players;

  PlayersComponent(this.players, {Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => new PlayersComponentState();
}

class PlayersComponentState extends State<PlayersComponent> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: new AnimatedList(
        key: widget.players.listKey,
        initialItemCount: widget.players.length,
        itemBuilder: (context, index, animation) => PlayerList.buildPlayer(
            widget.players, widget.players[index], context, animation),
      ),
      floatingActionButton: new FloatingActionButton(
        child: new Icon(Icons.add),
        onPressed: () => {},
      ),
    );
  }
}
