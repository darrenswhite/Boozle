import 'dart:collection';

import 'package:boozle/components/player/player.dart';
import 'package:boozle/components/player/player_card.dart';
import 'package:flutter/material.dart';

class PlayerList extends ListBase<Player> {
  final GlobalKey<AnimatedListState> listKey =
      new GlobalKey<AnimatedListState>();
  final List<Player> players = [
    new Player('Adam', Colors.yellow),
    new Player('Daniel', Colors.blue),
    new Player('Darren', Colors.orange),
  ];

  AnimatedListState get animatedList => listKey.currentState;

  static Widget buildPlayer(players, player, context, animation) {
    return new SizeTransition(
      sizeFactor: animation,
      child: new PlayerCard(players, player),
    );
  }

  @override
  void insert(int index, Player p) {
    players.insert(index, p);
    animatedList.insertItem(index);
  }

  @override
  Player removeAt(int index) {
    Player p = players.removeAt(index);

    if (p != null) {
      animatedList.removeItem(index,
          (context, animation) => buildPlayer(this, p, context, animation));
    }

    return p;
  }

  @override
  void operator []=(int index, Player p) {
    insert(index, p);
  }

  @override
  set length(int newLength) => players.length = newLength;

  @override
  Player operator [](int index) => players[index];

  @override
  int get length => players.length;
}
