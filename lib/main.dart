import 'package:boozle/components/game/game_component.dart';
import 'package:boozle/components/players/player_list.dart';
import 'package:boozle/components/players/players_component.dart';
import 'package:boozle/components/settings/settings_component.dart';
import 'package:boozle/config/application.dart';
import 'package:flutter/material.dart';

void main() => runApp(new Boozle());

class Boozle extends StatefulWidget {
  Boozle({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => new BoozleState();
}

class BoozleState extends State<Boozle> with SingleTickerProviderStateMixin {
  Map<Tab, Widget> tabs;
  TabController tabController;
  PlayerList players;

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: Application.TITLE,
      theme: new ThemeData(
        brightness: Brightness.dark,
        primarySwatch: Colors.orange,
        accentColor: Colors.orange,
        accentColorBrightness: Brightness.dark,
      ),
      home: new Scaffold(
        appBar: new AppBar(
          title: new TabBar(
            controller: tabController,
            tabs: tabs.keys.toList(),
          ),
        ),
        body: new TabBarView(
          children: tabs.values.toList(),
          controller: tabController,
        ),
        resizeToAvoidBottomPadding: false,
      ),
    );
  }

  @override
  void dispose() {
    tabController.dispose();
    super.dispose();
  }

  @override
  initState() {
    super.initState();
    players = new PlayerList();
    tabs = {
      new Tab(icon: const Icon(Icons.settings)):
          new SettingsComponent(),
      new Tab(icon: const Icon(Icons.gamepad)):
          new GameComponent(players),
      new Tab(icon: const Icon(Icons.people)):
          new PlayersComponent(players),
    };
    tabController = new TabController(
      initialIndex: 1,
      length: tabs.length,
      vsync: this,
    );
  }
}
