import 'dart:async';

import 'package:boozle/components/instance/game_component.dart';
import 'package:boozle/components/instance/instance.dart';
import 'package:boozle/components/settings/settings_component.dart';
import 'package:boozle/components/user/users.dart';
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
  Map<Tab, Widget> tabs;
  TabController tabController;

  @override
  Widget build(BuildContext context) {
    return new WillPopScope(
      onWillPop: _leaveInstance,
      child: new Scaffold(
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
    tabs = {
      new Tab(icon: const Icon(Icons.settings)):
          new SettingsComponent(widget.instanceHash),
      new Tab(icon: const Icon(Icons.gamepad)):
          new GameComponent(widget.instanceHash),
      new Tab(icon: const Icon(Icons.people)):
          new UsersComponent(widget.instanceHash),
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
