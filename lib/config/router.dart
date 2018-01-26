import 'package:boozle/components/lobby/lobby.dart';
import 'package:boozle/components/tabs/tabs.dart';
import 'package:flutter/material.dart';

class Router {
  static const String HOME = '/';
  static const String TABS = '/tabs';

  static final Map<String, Widget> _routes = {
    HOME: new LobbyComponent(),
    TABS: new TabsComponent(),
  };

  static Route<dynamic> generateRoute(RouteSettings settings) {
    return new PageRouteBuilder(
      settings: settings,
      pageBuilder: (context, animation, secondaryAnimation) {
        return _routes[settings.name];
      },
      transitionsBuilder: (_, Animation<double> animation, __, Widget child) {
        return new SlideTransition(
          position: new Tween<Offset>(
            begin: const Offset(1.0, 0.0),
            end: Offset.zero,
          )
              .animate(animation),
          child: child,
        );
      },
    );
  }
}
