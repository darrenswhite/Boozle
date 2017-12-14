import 'dart:math';

import 'package:boozle/components/game/action.dart';

class Application {
  static const String TITLE = 'Boozle';

  static const List<Action> ACTION_MODELS = const [
    const Action(
      name: 'Some action',
      description: 'Some description',
    ),
    const Action(
      name: 'Another action',
      description: 'Another description',
    )
  ];
}
