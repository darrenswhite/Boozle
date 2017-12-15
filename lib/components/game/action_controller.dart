import 'dart:math';

import 'package:boozle/components/game/action.dart';
import 'package:boozle/components/game/action_card.dart';
import 'package:boozle/components/players/player.dart';
import 'package:boozle/components/players/player_list.dart';
import 'package:boozle/config/application.dart';

class ActionController {
  final Random rnd = new Random();
  final PlayerList players;

  Action currentAction;
  Player currentPlayer;

  ActionController(this.players);

  ActionCard next() {
    currentAction = Application
        .ACTION_MODELS[rnd.nextInt(Application.ACTION_MODELS.length)];
    currentPlayer =
        players.length > 0 ? players[rnd.nextInt(players.length)] : null;

    return new ActionCard(currentAction, currentPlayer);
  }
}