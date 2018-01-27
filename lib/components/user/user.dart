import 'package:flutter/material.dart';

class User {
  const User(this.name, this.color);

  final String name;
  final Color color;

  @override
  String toString() {
    return 'User{name: $name, color: $color}';
  }
}
