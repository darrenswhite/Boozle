import 'package:boozle/config/application.dart';
import 'package:boozle/config/router.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const int kRoomCodeLength = 4;

class LobbyComponent extends StatefulWidget {
  @override
  _LobbyComponentState createState() => new _LobbyComponentState();
}

class _LobbyComponentState extends State<LobbyComponent> {
  final GlobalKey<FormState> _formKey = new GlobalKey<FormState>();
  String _roomCode;

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: new Column(
        children: <Widget>[
          _buildTitle(),
          _buildJoinForm(),
          new Expanded(child: new Container()),
          _buildNewButton(),
        ],
      ),
    );
  }

  Form _buildJoinForm() {
    return new Form(
      key: _formKey,
      child: new Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16.0),
        child: new Wrap(
          children: <Widget>[
            new TextFormField(
              decoration: const InputDecoration(
                icon: const Icon(Icons.code),
                labelText: 'Room code',
              ),
              inputFormatters: <TextInputFormatter>[
                new LengthLimitingTextInputFormatter(kRoomCodeLength),
                TextInputFormatter.withFunction((oldValue, newValue) {
                  return newValue.copyWith(text: newValue.text.toUpperCase());
                }),
              ],
              onSaved: (value) => _roomCode = value,
              validator: _validateRoomCode,
            ),
            new Container(
              margin: const EdgeInsets.symmetric(vertical: 20.0),
              constraints: new BoxConstraints.expand(
                  height: ButtonTheme.of(context).height),
              child: new RaisedButton(
                child: const Text('JOIN'),
                onPressed: _join,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNewButton() {
    return new Container(
      margin: const EdgeInsets.only(
        left: 5.0,
        right: 5.0,
        bottom: 5.0,
      ),
      constraints:
          new BoxConstraints.expand(height: ButtonTheme.of(context).height),
      child: new RaisedButton(
        child: const Text('NEW'),
        onPressed: _new,
      ),
    );
  }

  Widget _buildTitle() {
    return new Expanded(
      child: new Container(
        alignment: Alignment.center,
        child: new Text(
          Application.TITLE,
          style: Theme
              .of(context)
              .textTheme
              .display1
              .copyWith(color: Theme.of(context).primaryColor),
        ),
      ),
    );
  }

  void _join() {
    if (_formKey.currentState.validate()) {
      _formKey.currentState.save();
      Navigator.pushNamed(context, Router.TABS);
    }
  }

  void _new() {
    Navigator.pushNamed(context, Router.TABS);
  }

  String _validateRoomCode(String value) {
    if (value != null && value.length == kRoomCodeLength) {
      return null;
    }
    return 'Room code must be $kRoomCodeLength characters long';
  }
}
