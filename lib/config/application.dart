import 'package:boozle/components/game/action.dart';
import 'package:flutter/material.dart';

class Application {
  static const String TITLE = 'Boozle';

  static final ThemeData theme = new ThemeData(
    accentColor: Colors.deepOrange[500],
    accentColorBrightness: Brightness.dark,
    brightness: Brightness.light,
    iconTheme: new IconThemeData(color: Colors.deepOrange[500]),
    primarySwatch: Colors.deepOrange,
  );

  static const List<Action> ACTION_MODELS = const [
    const Action(
      name: 'Action A',
      description: 'This is a normal action',
    ),
    const Action(
      name: 'Action B',
      description: 'This action doesn\'t affect any player',
      affectsPlayer: false,
    ),
    const Action(
      name: 'Action C',
      description: 'This action has an image',
      image: 'images/lake.jpg',
    ),
    const Action(
      name: 'Action D',
      description: 'This action has an animated image',
      image: 'images/nyan_cat.gif',
    )
  ];
}
