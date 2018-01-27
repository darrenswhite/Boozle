import 'dart:math';

import 'package:boozle/components/instance/action.dart';
import 'package:boozle/components/instance/action_card.dart';
import 'package:boozle/components/player/player.dart';
import 'package:boozle/components/player/player_list.dart';
import 'package:boozle/config/application.dart';

class ActionController {
  ActionController(this.players);

  final Random rnd = new Random();
  final PlayerList players;

  Action currentAction;
  ActionCard currentActionCard;

  Player currentPlayer;

  void next() {
    currentAction = Application
        .ACTION_MODELS[rnd.nextInt(Application.ACTION_MODELS.length)];
    currentPlayer =
        players.length > 0 ? players[rnd.nextInt(players.length)] : null;

    currentActionCard = new ActionCard(currentAction, currentPlayer);
  }
}
