import 'package:boozle/components/instance/action_card.dart';
import 'package:boozle/components/user/user.dart';
import 'package:flutter/material.dart';
import 'package:meta/meta.dart';

class Action {
  const Action(
      {@required this.name,
      @required this.description,
      this.affectsUser = true,
      this.image});

  final String name;
  final String description;
  final bool affectsUser;
  final String image;

  ActionCard buildCard(User user) {
    return new ActionCard(this, user);
  }

  Widget buildImage() {
    if (image == null) {
      return new Container();
    } else {
      return new DecoratedBox(
        child: new Container(),
        decoration: new BoxDecoration(
          image: new DecorationImage(
            image: new AssetImage(image),
            fit: BoxFit.cover,
          ),
        ),
      );
    }
  }
}
