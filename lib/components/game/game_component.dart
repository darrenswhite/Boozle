import 'package:boozle/components/game/action_card.dart';
import 'package:boozle/components/game/action_controller.dart';
import 'package:boozle/components/players/player_list.dart';
import 'package:flutter/material.dart';

class GameComponent extends StatefulWidget {
  final ActionController controller;
  final PlayerList players;

  GameComponent(this.players, {Key key})
      : controller = new ActionController(players),
        super(key: key);

  @override
  GameComponentState createState() => new GameComponentState();
}

class GameComponentState extends State<GameComponent>
    with SingleTickerProviderStateMixin {
  AnimationController controller;
  Widget actionAnimation;
  ActionCard actionCard;

  animateAction(animation(Animation<double> animation), {dynamic callback()}) {
    setState(() {
      actionAnimation = animation(controller);

      if (callback == null) {
        controller.forward(from: 0.0);
      } else {
        controller.forward(from: 0.0).whenComplete(callback);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: new Center(
        child: actionAnimation,
      ),
      floatingActionButton: new FloatingActionButton(
        onPressed: nextAction,
        tooltip: 'Continue',
        child: new Icon(Icons.play_arrow),
      ),
    );
  }

  @override
  dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  initState() {
    super.initState();
    setNextAction();
    actionAnimation = actionCard;
    controller = new AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
  }

  nextAction() {
    animateAction(
      actionCard.slideOut,
      callback: () {
        setNextAction();
        animateAction(actionCard.slideIn);
      },
    );
  }

  setNextAction() {
    if (actionCard == null && widget.controller.currentAction != null) {
      actionCard = new ActionCard(
          widget.controller.currentAction, widget.controller.currentPlayer);
    } else {
      actionCard = widget.controller.next();
    }
  }
}
