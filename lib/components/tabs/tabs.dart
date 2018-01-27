import 'dart:async';

import 'package:boozle/components/instance/game_component.dart';
import 'package:boozle/components/instance/instance.dart';
import 'package:boozle/components/player/player_list.dart';
import 'package:boozle/components/player/players_component.dart';
import 'package:boozle/components/settings/settings_component.dart';
import 'package:boozle/config/auth.dart';
import 'package:flutter/material.dart';

class TabsComponent extends StatefulWidget {
  TabsComponent(this.instanceHash);

  final String instanceHash;

  @override
  State<StatefulWidget> createState() => new _TabsComponentState();
}

class _TabsComponentState extends State<TabsComponent>
    with SingleTickerProviderStateMixin {
  final GlobalKey<ScaffoldState> _scaffoldKey = new GlobalKey<ScaffoldState>();
  Map<Tab, Widget> tabs;
  TabController tabController;
  PlayerList players;

  @override
  Widget build(BuildContext context) {
    return new WillPopScope(
      onWillPop: _leaveInstance,
      child: new Scaffold(
        key: _scaffoldKey,
        appBar: new AppBar(
          elevation:
              Theme.of(context).platform == TargetPlatform.iOS ? 0.0 : 4.0,
          title: new Text('Instance code: ${widget.instanceHash}'),
          bottom: new TabBar(
            controller: tabController,
            tabs: tabs.keys.toList(),
          ),
        ),
        body: new TabBarView(
          children: tabs.values.toList(),
          controller: tabController,
        ),
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
      new Tab(icon: const Icon(Icons.settings)):
          new SettingsComponent(widget.instanceHash),
      new Tab(icon: const Icon(Icons.gamepad)):
          new GameComponent(widget.instanceHash),
      new Tab(icon: const Icon(Icons.people)):
          new PlayersComponent(widget.instanceHash),
    };
    tabController = new TabController(
      initialIndex: 1,
      length: tabs.length,
      vsync: this,
    );
  }

  Future<bool> _leaveInstance() async {
    final Instance instance =
        await Instance.fromDatabase(hash: widget.instanceHash);
    return instance.removePlayer(context, Auth.firebaseUser.uid);
  }
}
