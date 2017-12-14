import 'package:flutter/material.dart';

class SettingsComponent extends StatelessWidget {
  const SettingsComponent();

  @override
  Widget build(BuildContext context) {
    return new Center(
      child: new Text('Settings', style: Theme.of(context).textTheme.display1),
    );
  }
}
