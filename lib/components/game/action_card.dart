import 'package:boozle/components/game/action.dart';
import 'package:boozle/components/players/player.dart';
import 'package:flutter/material.dart';

class ActionCard extends StatelessWidget {
  static final tweenIn = new Tween<Offset>(
    begin: const Offset(-1.0, 0.0),
    end: Offset.zero,
  );
  static final tweenOut = new Tween<Offset>(
    begin: Offset.zero,
    end: const Offset(1.0, 0.0),
  );

  final Action action;
  final Player player;

  ActionCard(this.action, this.player, {Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MediaQuery.of(context).orientation == Orientation.landscape
        ? buildLandscape(context)
        : buildPortrait(context);
  }

  Widget buildLandscape(BuildContext context) {
    return new Text('TODO');
  }

  buildPortrait(BuildContext context) {
    Color playerColor =
        player != null && action.affectsPlayer ? player.color : null;
    List<Widget> widgets = [];

    if (action.image != null) {
      widgets.add(
        new Image.asset(
          action.image,
          fit: BoxFit.cover,
        ),
      );
    }

    widgets.add(new ListTile(
      title: new Text(action.name,
          style: new TextStyle(fontWeight: FontWeight.w500)),
      leading: new Icon(
        Icons.arrow_forward,
        color: playerColor,
      ),
    ));

    widgets.add(new Divider());

    if (player != null && action.affectsPlayer) {
      widgets.add(new ListTile(
        title: new Text(player.name,
            style: new TextStyle(fontWeight: FontWeight.w500)),
        leading: new Icon(
          Icons.contacts,
          color: playerColor,
        ),
      ));
    }

    widgets.add(new ListTile(
      title: new Text(action.description),
      leading: new Icon(
        Icons.description,
        color: playerColor,
      ),
    ));

    return new Card(
      child: new Column(
        mainAxisSize: MainAxisSize.min,
        children: widgets,
      ),
    );
  }

  Widget slide(Animation<double> parent, Tween<Offset> tween, Curve curve) {
    return new SlideTransition(
      position: tween.animate(new CurvedAnimation(
        parent: parent,
        curve: curve,
      )),
      child: this,
    );
  }

  Widget slideIn(Animation<double> parent) {
    return slide(parent, tweenIn, Curves.elasticOut);
  }

  Widget slideOut(Animation<double> parent) {
    return slide(parent, tweenOut, Curves.elasticIn);
  }
}
