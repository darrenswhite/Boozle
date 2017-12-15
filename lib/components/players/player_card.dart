import 'package:boozle/components/players/player.dart';
import 'package:boozle/components/players/player_list.dart';
import 'package:flutter/material.dart';

class PlayerCard extends StatefulWidget {
  final PlayerList players;
  final Player player;

  PlayerCard(this.players, this.player, {Key key}) : super(key: key);

  @override
  State<PlayerCard> createState() => new PlayerCardState();
}

class PlayerCardState extends State<PlayerCard> {
  TextEditingController controller;
  FocusNode focusNode;
  bool isEditting = false;

  @override
  Widget build(BuildContext context) {
    return new Card(
      child: new ListTile(
        leading: new Icon(
          Icons.person,
          color: widget.player.color,
        ),
        title: isEditting
            ? new TextField(
                focusNode: focusNode,
                controller: controller,
                style: new TextStyle(fontWeight: FontWeight.w500),
                onSubmitted: (value) => updateName,
              )
            : new Text(widget.player.name,
                style: new TextStyle(fontWeight: FontWeight.w500)),
        trailing: new Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: <Widget>[
            new IconButton(
              icon: new Icon(Icons.edit),
              color: Theme.of(context).accentColor,
              onPressed: updateName,
            ),
            new IconButton(
              icon: new Icon(Icons.remove),
              color: Theme.of(context).accentColor,
              onPressed: () => setState(() {
                    widget.players
                        .removeAt(widget.players.indexOf(widget.player));
                  }),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    controller.dispose();
    focusNode.dispose();
    super.dispose();
  }

  @override
  initState() {
    super.initState();
    controller = new TextEditingController(text: widget.player.name);
    focusNode = new FocusNode();
    focusNode.addListener(() {
      if (!focusNode.hasFocus) {
        updateName();
      }
    });
  }

  updateName({value}) {
    setState(() {
      widget.player.name = value != null ? value : controller.text;
      isEditting = !isEditting;
    });
  }
}