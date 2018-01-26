import 'package:boozle/components/boozle/boozle.dart';
import 'package:flutter/material.dart';
import 'package:logging/logging.dart';

void main() {
  Logger.root.level = Level.ALL;
  Logger.root.onRecord.listen((LogRecord rec) {
    print('${rec.level.name}: ${rec.loggerName}: ${rec.time}: ${rec.message}');
  });
  runApp(new BoozleComponent());
}
