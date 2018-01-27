import 'package:boozle/components/instance/action.dart';
import 'package:boozle/components/instance/action_card.dart';
import 'package:boozle/components/instance/action_controller.dart';
import 'package:boozle/components/players/player_list.dart';
import 'package:flutter/material.dart';

class GameComponent extends StatefulWidget {
  GameComponent(this.players, {Key key})
      : controller = new ActionController(players),
        super(key: key);

  final ActionController controller;
  final PlayerList players;

  @override
  GameComponentState createState() => new GameComponentState();
}

class GameComponentState extends State<GameComponent>
    with SingleTickerProviderStateMixin {
  AnimationController controller;
  Widget actionAnimation;
  Widget imageAnimation, prevImageAnimation;
  DecoratedBox imageWidget, prevImageWidget;
  ActionCard actionCard;
  Action action;
  String prevImage;
  bool diffImage = false;

  void animateAction() {
    widget.controller.next();

    setState(() {
      animateActionOut(callback: () {
        actionCard = new ActionCard(
            widget.controller.currentAction, widget.controller.currentPlayer);
        imageWidget = getActionImage();
        setState(() {
          animateActionIn(callback: () {
            prevImageAnimation = null;
            prevImageWidget = imageWidget;
          });
        });
      });
    });
  }

  void animateActionIn({dynamic callback()}) {
    actionAnimation = new SlideTransition(
      position: new Tween<Offset>(
        begin: const Offset(-1.0, 0.0),
        end: Offset.zero,
      )
          .animate(new CurvedAnimation(
        parent: controller,
        curve: Curves.elasticOut,
      )),
      child: actionCard,
    );

    imageAnimation = new FadeTransition(
      opacity: new Tween<double>(
        begin: 0.0,
        end: 1.0,
      )
          .animate(controller),
      child: imageWidget,
    );

    if (callback == null) {
      controller.forward(from: 0.0);
    } else {
      controller.forward(from: 0.0).whenComplete(callback);
    }
  }

  void animateActionOut({dynamic callback()}) {
    actionAnimation = new SlideTransition(
      position: new Tween<Offset>(
        begin: Offset.zero,
        end: const Offset(1.0, 0.0),
      )
          .animate(new CurvedAnimation(
        parent: controller,
        curve: Curves.elasticIn,
      )),
      child: actionCard,
    );

    imageAnimation = new FadeTransition(
      opacity: new Tween<double>(
        begin: 1.0,
        end: 0.0,
      )
          .animate(controller),
      child: imageWidget,
    );

    if (callback == null) {
      controller.forward(from: 0.0);
    } else {
      controller.forward(from: 0.0).whenComplete(callback);
    }
  }

  @override
  Widget build(BuildContext context) {
    List<Widget> stackWidgets = [];

    if (imageAnimation != null) {
      stackWidgets.add(imageAnimation);
    }

    stackWidgets.add(new Center(
      child: actionAnimation,
    ));

    return new Scaffold(
      body: new Stack(
        children: stackWidgets,
      ),
      floatingActionButton: new FloatingActionButton(
        onPressed: animateAction,
        tooltip: 'Continue',
        child: const Icon(Icons.play_arrow),
      ),
    );
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  DecoratedBox getActionImage() {
    Widget image;

    if (widget.controller.currentAction.image != null) {
      image = new DecoratedBox(
        child: new Container(),
        decoration: new BoxDecoration(
          image: new DecorationImage(
            image: new AssetImage(widget.controller.currentAction.image),
            fit: BoxFit.cover,
          ),
        ),
      );
    }

    return image;
  }

  @override
  void initState() {
    super.initState();
    widget.controller.next();
    actionAnimation = actionCard = new ActionCard(
        widget.controller.currentAction, widget.controller.currentPlayer);
    imageAnimation = imageWidget = getActionImage();
    controller = new AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
  }
}
