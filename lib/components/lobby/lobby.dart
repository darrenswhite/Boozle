import 'dart:async';

import 'package:boozle/config/application.dart';
import 'package:boozle/config/database.dart';
import 'package:boozle/config/router.dart';
import 'package:boozle/util/hashids.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:logging/logging.dart';

const String kHashAlphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';
const int kMinHashLength = 4;

Hashids _hash = new Hashids(
  alphabet: kHashAlphabet,
  minHashLength: kMinHashLength,
);

class LobbyComponent extends StatefulWidget {
  @override
  _LobbyComponentState createState() => new _LobbyComponentState();
}

class _LobbyComponentState extends State<LobbyComponent> {
  static final Logger log = new Logger('_LobbyComponentState');
  final GlobalKey<ScaffoldState> _scaffoldKey = new GlobalKey<ScaffoldState>();
  final GlobalKey<FormState> _formKey = new GlobalKey<FormState>();
  bool authorised = false;
  String _instanceHash;

  @override
  Widget build(BuildContext context) {
    if (authorised) {
      return new Scaffold(
        key: _scaffoldKey,
        body: new Column(
          children: <Widget>[
            _buildTitle(),
            _buildJoinForm(),
            new Expanded(child: new Container()),
            _buildNewButton(),
          ],
        ),
      );
    } else {
      return new Scaffold(
        body: new Center(
          child: new Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              new CircularProgressIndicator(),
            ],
          ),
        ),
      );
    }
  }

  @override
  void initState() {
    super.initState();
    FirebaseAuth.instance.signInAnonymously().then((_) {
      setState(() => authorised = true);
    });
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
                labelText: 'Instance code',
              ),
              inputFormatters: <TextInputFormatter>[
                TextInputFormatter.withFunction((oldValue, newValue) {
                  return newValue.copyWith(text: newValue.text.toUpperCase());
                }),
                new WhitelistingTextInputFormatter(
                    new RegExp('[$kHashAlphabet]')),
              ],
              onSaved: (value) => _instanceHash = value,
              validator: _validateInstanceHash,
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
    log.info('Validating form');
    if (_formKey.currentState.validate()) {
      log.info('Saving form');
      _formKey.currentState.save();
      _navigateTabs(_instanceHash);
    }
  }

  void _navigateTabs(String hash) {
    log.info('Navigiating to tabs with instance hash: $hash');
    Navigator.pushNamed(context, Router.TABS + hash);
  }

  /// New instances have hashes based off the number of existing instances
  Future<Null> _new() async {
    String hash;

    final TransactionResult transactionResult =
        await Database.instancesRef.runTransaction((data) async {
      final List<dynamic> instances = new List.from(data.value ?? []);
      int index = instances.indexOf(null);

      if (index == -1) {
        index = instances.length;
      } else {
        instances.removeAt(index);
      }

      hash = _hash.encode([index]);
      instances.insert(index, {'hash': hash});

      data.value = instances;

      return data;
    });

    if (transactionResult.committed) {
      _navigateTabs(hash);
    } else {
      _scaffoldKey.currentState.showSnackBar(new SnackBar(
        content: new Text('Unable to create new instance'),
      ));
      if (transactionResult.error != null) {
        log.severe(transactionResult.error.message);
      }
    }
  }

  String _validateInstanceHash(String value) {
    if (value != null && value.length == kMinHashLength) {
      return null;
    } else {
      // TODO Validate that the hash exists in the database
      // See: https://github.com/flutter/flutter/issues/9688
    }
    return 'Room code must be $kMinHashLength characters long';
  }
}
