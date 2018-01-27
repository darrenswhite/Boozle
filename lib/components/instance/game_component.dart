import 'dart:math';

import 'package:boozle/components/instance/action.dart';
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
  final List<User> users = <User>[];
  AnimationController controller;
  Widget actionAnimation;

  Action currAction, prevAction;
  User currUser, prevUser;

  void animateAction() {
    next();

    setState(() {
      animateActionOut(callback: () {
        setState(() {
          animateActionIn();
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
      child: currAction.buildCard(currUser),
    );

    if (callback == null) {
      controller.forward(from: 0.0);
    } else {
      controller.forward(from: 0.0).whenComplete(callback);
    }
  }

  void animateActionOut({dynamic callback()}) {
    if (prevAction == null) {
      if (callback != null) {
        callback();
      }
      return;
    }

    actionAnimation = new SlideTransition(
      position: new Tween<Offset>(
        begin: Offset.zero,
        end: const Offset(1.0, 0.0),
      )
          .animate(new CurvedAnimation(
        parent: controller,
        curve: Curves.elasticIn,
      )),
      child: prevAction.buildCard(prevUser),
    );

    if (callback == null) {
      controller.forward(from: 0.0);
    } else {
      controller.forward(from: 0.0).whenComplete(callback);
    }
  }

  void next() {
    prevAction = currAction;
    prevUser = currUser;

    currAction = Application
        .ACTION_MODELS[widget.rnd.nextInt(Application.ACTION_MODELS.length)];
    currUser =
        users.length > 0 ? users[widget.rnd.nextInt(users.length)] : null;
  }

  @override
  Widget build(BuildContext context) {
    List<Widget> stackWidgets = [];

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

  @override
  void initState() {
    super.initState();
    controller = new AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    animateAction();
  }
}
