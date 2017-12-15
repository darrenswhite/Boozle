import 'dart:math';

import 'package:boozle/components/players/player.dart';
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
  addPlayer() async {
    TextEditingController controller = new TextEditingController();
    String input = await showDialog<String>(
      context: context,
      child: new AlertDialog(
        title: new Text('Add new player'),
        content: new TextField(
          decoration: new InputDecoration(labelText: 'Name'),
          controller: controller,
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
    );

    if (input != null) {
      widget.players.insert(
          widget.players.length,
          new Player(
              input,
              new Color.fromARGB(255, widget.rnd.nextInt(256),
                  widget.rnd.nextInt(256), widget.rnd.nextInt(256))));
    }
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
        child: new Icon(Icons.add),
        onPressed: addPlayer,
      ),
    );
  }
}
