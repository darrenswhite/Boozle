import 'dart:math';

import 'package:boozle/components/instance/action.dart';
import 'package:boozle/components/instance/action_card.dart';
import 'package:boozle/components/user/user.dart';
import 'package:boozle/config/application.dart';
import 'package:flutter/material.dart';

class GameComponent extends StatefulWidget {
  GameComponent(this.instanceHash);

  final Random rnd = new Random();
  final String instanceHash;

  @override
  _GameComponentState createState() => new _GameComponentState();
}

class _GameComponentState extends State<GameComponent>
    with SingleTickerProviderStateMixin {
  AnimationController controller;
  Widget actionAnimation;
  Widget imageAnimation, prevImageAnimation;
  DecoratedBox imageWidget, prevImageWidget;
  ActionCard actionCard;
  Action action;
  String prevImage;
  bool diffImage = false;

  final List<User> users = <User>[];

  Action currentAction;
  ActionCard currentActionCard;
  User currentUser;

  void next() {
    currentAction = Application
        .ACTION_MODELS[widget.rnd.nextInt(Application.ACTION_MODELS.length)];
    currentUser =
        users.length > 0 ? users[widget.rnd.nextInt(users.length)] : null;

    currentActionCard = new ActionCard(currentAction, currentUser);
  }

  void animateAction() {
    next();

    setState(() {
      animateActionOut(callback: () {
        actionCard = new ActionCard(currentAction, currentUser);
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

    if (currentAction.image != null) {
      image = new DecoratedBox(
        child: new Container(),
        decoration: new BoxDecoration(
          image: new DecorationImage(
            image: new AssetImage(currentAction.image),
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
    next();
    actionAnimation = actionCard = new ActionCard(currentAction, currentUser);
    imageAnimation = imageWidget = getActionImage();
    controller = new AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
  }
}
