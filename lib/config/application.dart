import 'package:boozle/components/game/action.dart';

class Application {
  static const String TITLE = 'Boozle';

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
  ];
}
