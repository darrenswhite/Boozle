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

  @override
  String toString() {
    return 'Action{name: $name, description: $description, '
        'affectsUser: $affectsUser, image: $image}';
  }
}
