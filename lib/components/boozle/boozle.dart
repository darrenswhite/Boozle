import 'package:boozle/config/application.dart';
import 'package:boozle/config/router.dart';
import 'package:flutter/material.dart';

class BoozleComponent extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      onGenerateRoute: Router.generateRoute,
      theme: Application.theme,
      title: Application.TITLE,
    );
  }
}
