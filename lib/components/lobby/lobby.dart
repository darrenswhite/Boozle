import 'dart:async';

import 'package:boozle/config/application.dart';
import 'package:boozle/config/database.dart';
import 'package:boozle/config/router.dart';
import 'package:boozle/instance/instance.dart';
import 'package:boozle/util/hashids.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:firebase_database/ui/utils/stream_subscriber_mixin.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:logging/logging.dart';

const String kHashAlphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';
const int kMinHashLength = 4;

final Hashids _hash = new Hashids(
  alphabet: kHashAlphabet,
  minHashLength: kMinHashLength,
);

class LobbyComponent extends StatefulWidget {
  @override
  _LobbyComponentState createState() => new _LobbyComponentState();
}

class _LobbyComponentState extends State<LobbyComponent>
    with StreamSubscriberMixin<Event> {
  static final Logger log = new Logger('_LobbyComponentState');
  final GlobalKey<ScaffoldState> _scaffoldKey = new GlobalKey<ScaffoldState>();
  final GlobalKey<FormState> _formKey = new GlobalKey<FormState>();
  final List<Instance> _instances = <Instance>[];
  bool _authorised = false;
  bool _disabled = false;
  String _instanceHash;

  @override
  Widget build(BuildContext context) {
    if (_authorised) {
      return new Scaffold(
        key: _scaffoldKey,
        body: new Column(
          children: <Widget>[
            _buildTitle(),
            _buildJoinForm(disabled: _disabled),
            new Expanded(child: new Container()),
            _buildNewButton(disabled: _disabled),
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
  void dispose() {
    cancelSubscriptions();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    FirebaseAuth.instance.signInAnonymously().then((_) {
      setState(() => _authorised = true);
    });
    listen(Database.instancesRef.onChildAdded, (event) {
      _instances.add(new Instance.snapshot(event.snapshot));
    });
    listen(Database.instancesRef.onChildRemoved, (event) {
      _instances.remove(new Instance.snapshot(event.snapshot));
    });
  }

  Form _buildJoinForm({bool disabled: false}) {
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
                onPressed: disabled ? null : _join,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNewButton({bool disabled: false}) {
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
        onPressed: disabled ? null : _new,
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
    try {
      setState(() => _disabled = true);
      log.info('Validating form');
      if (_formKey.currentState.validate()) {
        log.info('Saving form');
        _formKey.currentState.save();
        setState(() => _disabled = false);
        _navigateTabs(_instanceHash);
      } else {
        log.warning('Validation failed');
      }
    } finally {
      setState(() => _disabled = false);
    }
  }

  void _navigateTabs(String hash) {
    log.info('Navigiating to tabs with instance hash: $hash');
    Navigator.pushNamed(context, Router.TABS + hash);
  }

  /// New instances have index-based hashes
  /// Old instances are replaced before creating new instance hashes
  Future<Null> _new() async {
    try {
      String hash;
      setState(() => _disabled = true);
      final TransactionResult transactionResult =
          await Database.instancesRef.runTransaction((data) async {
        final List<dynamic> instances = new List.from(data.value ?? []);
        int index = instances.indexOf(null);

        log.fine('Total number of instances: ${instances.length}');

        if (index == -1) {
          index = instances.length;
        } else {
          instances.removeAt(index);
        }

        log.fine('Adding new instance at: $index');

        hash = _hash.encode([index]);

        log.fine('New instance hash: $hash');

        instances.insert(index, {'hash': hash});

        data.value = instances;

        return data;
      });

      if (transactionResult.committed) {
        log.info('New instance successfully created: $hash');
        _navigateTabs(hash);
      } else {
        log.warning('Failed to create new instance: $hash');
        _scaffoldKey.currentState.showSnackBar(new SnackBar(
          content: new Text('Unable to create new instance'),
        ));
        if (transactionResult.error != null) {
          log.severe(transactionResult.error.message);
        }
      }
    } finally {
      setState(() => _disabled = false);
    }
  }

  String _validateInstanceHash(String hash) {
    if (hash != null && _instances.any((i) => i.hash == hash)) {
      return null;
    } else {
      log.warning('Instance hash does not exist: $hash');
      return 'Invalid instance code';
    }
  }
}
