import 'package:boozle/components/instance/action.dart';
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
      description: 'This action doesn\'t affect any user',
      affectsUser: false,
    ),
  ];
}
