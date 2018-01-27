import 'package:meta/meta.dart';

class Action {
  const Action(
      {@required this.name,
      @required this.description,
      this.affectsPlayer = true,
      this.image});

  final String name;
  final String description;
  final bool affectsPlayer;
  final String image;
}
