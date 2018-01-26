import 'package:boozle/components/game/game_component.dart';
import 'package:boozle/components/players/player_list.dart';
import 'package:boozle/components/players/players_component.dart';
import 'package:boozle/components/settings/settings_component.dart';
import 'package:flutter/material.dart';

class TabsComponent extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => new _TabsComponentState();
}

class _TabsComponentState extends State<TabsComponent>
    with SingleTickerProviderStateMixin {
  Map<Tab, Widget> tabs;
  TabController tabController;
  PlayerList players;

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        elevation: Theme.of(context).platform == TargetPlatform.iOS ? 0.0 : 4.0,
        title: new TabBar(
          controller: tabController,
          tabs: tabs.keys.toList(),
        ),
      ),
      body: new TabBarView(
        children: tabs.values.toList(),
        controller: tabController,
      ),
    );
  }

  @override
  void dispose() {
    tabController.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    players = new PlayerList();
    tabs = {
      new Tab(icon: const Icon(Icons.settings)): new SettingsComponent(),
      new Tab(icon: const Icon(Icons.gamepad)): new GameComponent(players),
      new Tab(icon: const Icon(Icons.people)): new PlayersComponent(players),
    };
    tabController = new TabController(
      initialIndex: 1,
      length: tabs.length,
      vsync: this,
    );
  }
}
