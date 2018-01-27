import 'dart:math';

import 'package:boozle/components/instance/action.dart';
import 'package:boozle/components/instance/action_card.dart';
import 'package:boozle/components/instance/instance.dart';
import 'package:boozle/components/user/user.dart';
import 'package:boozle/config/application.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/utils/stream_subscriber_mixin.dart';
import 'package:flutter/material.dart';
import 'package:logging/logging.dart';

class GameComponent extends StatefulWidget {
  GameComponent(this.instanceHash);

  final Random rnd = new Random();
  final String instanceHash;

  @override
  _GameComponentState createState() => new _GameComponentState();
}

class _GameComponentState extends State<GameComponent>
    with SingleTickerProviderStateMixin, StreamSubscriberMixin<Event> {
  static Logger log = new Logger('_GameComponentState');
  final List<User> _users = <User>[];
  AnimationController _animation;
  ActionCard _currCard, _prevCard;
  Animation<Offset> _cardIn, _cardOut;

  @override
  Widget build(BuildContext context) {
    List<Widget> stackWidgets = [];

    if (_currCard != null) {
      stackWidgets.add(_buildAnimatedWidget(_currCard, _cardIn));
      if (_prevCard != null) {
        stackWidgets.add(_buildAnimatedWidget(_prevCard, _cardOut));
      } else {
        stackWidgets.add(_buildPlaceholder());
      }
    } else {
      stackWidgets.add(_buildPlaceholder());
    }

    return new Scaffold(
      body: new Stack(
        children: stackWidgets,
      ),
      floatingActionButton: new FloatingActionButton(
        onPressed: next,
        tooltip: 'Continue',
        child: const Icon(Icons.play_arrow),
      ),
    );
  }

  Widget _buildAnimatedWidget(Widget child, Animation<Offset> animation) {
    return new Center(
      child: new AnimatedBuilder(
        child: child,
        animation: animation,
        builder: (BuildContext context, Widget child) {
          return new SlideTransition(
            position: animation,
            child: child,
          );
        },
      ),
    );
  }

  Widget _buildPlaceholder() {
    return _buildAnimatedWidget(
      new Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          new Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              new Text('Tap '),
              new Icon(Icons.play_circle_filled),
              new Text(' to start'),
            ],
          ),
        ],
      ),
      _cardOut,
    );
  }

  @override
  void dispose() {
    _animation.dispose();
    cancelSubscriptions();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    _animation = new AnimationController(
      duration: const Duration(milliseconds: 2000),
      vsync: this,
    );
    _cardIn = new Tween<Offset>(
      begin: const Offset(-1.0, 0.0),
      end: Offset.zero,
    )
        .animate(new CurvedAnimation(
      parent: _animation,
      curve: new Interval(0.5, 1.0, curve: Curves.elasticOut),
    ));
    _cardOut = new Tween<Offset>(
      begin: Offset.zero,
      end: const Offset(1.0, 0.0),
    )
        .animate(new CurvedAnimation(
      parent: _animation,
      curve: new Interval(0.0, 0.5, curve: Curves.elasticIn),
    ));
    DatabaseReference usersRef =
        Instance.reference(hash: widget.instanceHash).child(Instance.KEY_USERS);
    listen(usersRef.onChildAdded, (event) {
      _users.add(new User.snapshot(event.snapshot));
    });
    listen(usersRef.onChildRemoved, (event) {
      _users.remove(new User.snapshot(event.snapshot));
    });
  }

  void next() {
    final Action nextAction = Application
        .ACTION_MODELS[widget.rnd.nextInt(Application.ACTION_MODELS.length)];
    final User nextUser =
        _users.length > 0 ? _users[widget.rnd.nextInt(_users.length)] : null;

    log.info('Next action: $nextAction');
    log.info('Next user: $nextUser');

    _prevCard = _currCard;
    _currCard = new ActionCard(nextAction, nextUser);

    setState(() {
      _animation.forward(from: 0.0);
    });
  }
}
