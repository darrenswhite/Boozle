import 'package:boozle/components/instance/action.dart';
import 'package:boozle/components/user/user.dart';
import 'package:flutter/material.dart';

class ActionCard extends StatelessWidget {
  ActionCard(this.action, this.user);

  final Action action;
  final User user;

  @override
  Widget build(BuildContext context) {
    Color userColor =
        user != null && action.affectsUser ? user.color : null;
    List<Widget> columnWidgets = [];

    columnWidgets.add(new ListTile(
      title: new Text(action.name,
          style: new TextStyle(fontWeight: FontWeight.w500)),
      leading: new Icon(
        Icons.arrow_forward,
        color: userColor,
      ),
    ));

    columnWidgets.add(new Divider());

    if (user != null && action.affectsUser) {
      columnWidgets.add(new ListTile(
        title: new Text(user.name,
            style: new TextStyle(fontWeight: FontWeight.w500)),
        leading: new Icon(
          Icons.contacts,
          color: userColor,
        ),
      ));
    }

    columnWidgets.add(new ListTile(
      title: new Text(action.description),
      leading: new Icon(
        Icons.description,
        color: userColor,
      ),
    ));

    return new Card(
      child: new Column(
        mainAxisAlignment: MainAxisAlignment.center,
        mainAxisSize: MainAxisSize.min,
        children: columnWidgets,
      ),
    );
  }
}
