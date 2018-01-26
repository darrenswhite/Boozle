import 'package:boozle/components/lobby/lobby.dart';
import 'package:boozle/components/tabs/tabs.dart';
import 'package:flutter/material.dart';

typedef Widget ValueWidgetBuilder(BuildContext context, String value);

class Router {
  static const String HOME = '/';
  static const String TABS = '/tabs:';

  static final Map<String, ValueWidgetBuilder> _routes = {
    HOME: (context, _) => new LobbyComponent(),
    TABS: (context, v) => new TabsComponent(v),
  };

  static Route<dynamic> generateRoute(RouteSettings settings) {
    return new PageRouteBuilder(
      settings: settings,
      pageBuilder: (context, animation, secondaryAnimation) {
        final String name = settings.name;
        final List<String> path = name.split(':');
        if (path.length == 1) {
          return _routes[path[0]](context, null);
        } else {
          return _routes['${path[0]}:'](context, path[1]);
        }
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
