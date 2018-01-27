import 'package:flutter/material.dart';

class SettingsComponent extends StatelessWidget {
  SettingsComponent(this.instanceHash);

  final String instanceHash;

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: new Center(
        child: new Text(
          'Settings',
          style: Theme.of(context).textTheme.display1,
        ),
      ),
    );
  }
}
