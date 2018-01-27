import 'dart:math';

import 'package:boozle/components/instance/action.dart';
import 'package:boozle/components/instance/action_card.dart';
import 'package:boozle/components/user/user.dart';
import 'package:boozle/config/application.dart';

class ActionController {
  ActionController(this.instanceHash);

  final Random rnd = new Random();
  final String instanceHash;
  final List<User> users = <User>[];

  Action currentAction;
  ActionCard currentActionCard;
  User currentUser;

  void next() {
    currentAction = Application
        .ACTION_MODELS[rnd.nextInt(Application.ACTION_MODELS.length)];
    currentUser = users.length > 0 ? users[rnd.nextInt(users.length)] : null;

    currentActionCard = new ActionCard(currentAction, currentUser);
  }
}
