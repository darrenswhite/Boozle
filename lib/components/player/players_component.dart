import 'dart:math';

import 'package:boozle/components/player/player.dart';
import 'package:boozle/components/player/player_list.dart';
import 'package:flutter/material.dart';

class PlayersComponent extends StatefulWidget {
  PlayersComponent(this.players, {Key key}) : super(key: key);

  final Random rnd = new Random();
  final PlayerList players;

  @override
  State<StatefulWidget> createState() => new PlayersComponentState();
}

class PlayersComponentState extends State<PlayersComponent> {
  void addPlayer() {
    TextEditingController controller = new TextEditingController();
    showDialog<String>(
      context: context,
      child: new AlertDialog(
        title: new Text('Add new player'),
        titlePadding: new EdgeInsets.only(
          left: 15.0,
          top: 15.0,
          right: 15.0,
          bottom: 0.0,
        ),
        content: new TextField(
          decoration: new InputDecoration(labelText: 'Name'),
          controller: controller,
        ),
        contentPadding: new EdgeInsets.only(
          left: 15.0,
          top: 0.0,
          right: 15.0,
          bottom: 0.0,
        ),
        actions: <Widget>[
          new FlatButton(
            child: new Text('Cancel'),
            onPressed: () => Navigator.pop(context),
          ),
          new FlatButton(
            child: new Text('Add'),
            onPressed: () => Navigator.pop(context, controller.text),
          )
        ],
      ),
    ).then((input) {
      if (input != null) {
        widget.players.insert(
            widget.players.length,
            new Player(
                input,
                new Color.fromARGB(255, widget.rnd.nextInt(256),
                    widget.rnd.nextInt(256), widget.rnd.nextInt(256))));
      }
    });
  }

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
        child: const Icon(Icons.add),
        onPressed: addPlayer,
      ),
    );
  }
}
