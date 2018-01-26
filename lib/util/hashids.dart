/// http://hashids.org/

const String kDefaultAlphabet =
    'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';
const String kDefaultSeps = 'cfhistuCFHISTU';
const String kDefaultSalt = '';

const int kDefaultMinHashLength = 0;
const int kMinAlphabetLength = 16;
const double kSepDiv = 3.5;
const int kGuardDiv = 12;

class Hashids {
  const Hashids._(
    this.salt,
    this.minHashLength,
    this.alphabet,
    this._seps,
    this._guards,
  );

  final String salt;
  final int minHashLength;
  final String alphabet;
  final String _seps;
  final String _guards;

  factory Hashids(
      {String salt = kDefaultSalt,
      int minHashLength = 0,
      String alphabet = kDefaultAlphabet}) {
    String uniqueAlphabet = '';
    for (int i = 0; i < alphabet.length; i++) {
      if (uniqueAlphabet.indexOf(alphabet[i]) == -1) {
        uniqueAlphabet += alphabet[i];
      }
    }

    alphabet = uniqueAlphabet.toString();

    if (alphabet.length < kMinAlphabetLength) {
      throw new ArgumentError(
          'alphabet must contain at least $kMinAlphabetLength unique characters');
    }

    if (alphabet.contains(' ')) {
      throw new ArgumentError('alphabet cannot contains spaces');
    }

    // seps should contain only characters present in alphabet;
    // alphabet should not contains seps
    String seps = kDefaultSeps;
    for (int i = 0; i < seps.length; i++) {
      final int j = alphabet.indexOf(seps[i]);
      if (j == -1) {
        seps = seps.substring(0, i) + ' ' + seps.substring(i + 1);
      } else {
        alphabet = alphabet.substring(0, j) + ' ' + alphabet.substring(j + 1);
      }
    }

    alphabet = alphabet.replaceAll(new RegExp(r'\s+'), '');
    seps = seps.replaceAll(new RegExp(r'\s+'), '');
    seps = consistentShuffle(seps, salt);

    if (seps.isEmpty || alphabet.length / seps.length > kSepDiv) {
      int sepsLen = (alphabet.length / kSepDiv).ceil();

      if (sepsLen == 1) {
        sepsLen++;
      }

      if (sepsLen > seps.length) {
        final int diff = sepsLen - seps.length;
        seps += alphabet.substring(0, diff);
        alphabet = alphabet.substring(diff);
      } else {
        seps = seps.substring(0, sepsLen);
      }
    }

    alphabet = consistentShuffle(alphabet, salt);
    final int guardCount = (alphabet.length / kGuardDiv).ceil();

    String guards;
    if (alphabet.length < 3) {
      guards = seps.substring(0, guardCount);
      seps = seps.substring(guardCount);
    } else {
      guards = alphabet.substring(0, guardCount);
      alphabet = alphabet.substring(guardCount);
    }

    return new Hashids._(salt, minHashLength, alphabet, seps, guards);
  }

  String encode(List<int> numbers) {
    if (numbers.length == 0) {
      return '';
    }

    for (final int number in numbers) {
      if (number < 0) {
        return '';
      }
    }
    return _encode(numbers);
  }

  List<int> decode(String hash) {
    if (hash.isEmpty) {
      return <int>[];
    }

    final String validChars = alphabet + _guards + _seps;
    for (int i = 0; i < hash.length; i++) {
      if (validChars.indexOf(hash[i]) == -1) {
        return <int>[];
      }
    }

    return _decode(hash, alphabet);
  }

  String encodeHex(String hexa) {
    if (!new RegExp(r'^[0-9a-fA-F]+$').hasMatch(hexa)) {
      return '';
    }

    final List<int> matched = new List<int>();
    final Iterable<Match> matcher =
        new RegExp(r'[\w\W]{1,12}').allMatches(hexa);

    for (Match match in matcher) {
      matched.add(int.parse('1${match.group(0)}', radix: 16));
    }

    return encode(matched);
  }

  String decodeHex(String hash) {
    String result = '';
    final List<int> numbers = decode(hash);

    for (final int number in numbers) {
      result += number.toRadixString(16).substring(1);
    }

    return result.toString();
  }

  String _encode(List<int> numbers) {
    int numberHashInt = 0;
    for (int i = 0; i < numbers.length; i++) {
      numberHashInt += (numbers[i] % (i + 100));
    }
    String alphabet = this.alphabet;
    final String ret = alphabet[numberHashInt % alphabet.length];

    int num;
    int sepsIndex, guardIndex;
    String buffer;
    String retStrB = '';
    retStrB += ret;
    String guard;

    for (int i = 0; i < numbers.length; i++) {
      num = numbers[i];
      buffer = ret + salt + alphabet;

      alphabet = Hashids.consistentShuffle(
          alphabet, buffer.substring(0, alphabet.length));
      final String last = Hashids.hash(num, alphabet);

      retStrB += last;

      if (i + 1 < numbers.length) {
        if (last.length > 0) {
          num %= (last.codeUnitAt(0) + i);
          sepsIndex = num % _seps.length;
        } else {
          sepsIndex = 0;
        }
        retStrB += _seps[sepsIndex];
      }
    }

    String retStr = retStrB.toString();
    if (retStr.length < minHashLength) {
      guardIndex = (numberHashInt + (retStr.codeUnitAt(0))) % _guards.length;
      guard = _guards[guardIndex];

      retStr = guard + retStr;

      if (retStr.length < minHashLength) {
        guardIndex = (numberHashInt + (retStr.codeUnitAt(2))) % _guards.length;
        guard = _guards[guardIndex];

        retStr += guard;
      }
    }

    final int halfLen = alphabet.length ~/ 2;
    while (retStr.length < minHashLength) {
      alphabet = Hashids.consistentShuffle(alphabet, alphabet);
      retStr =
          alphabet.substring(halfLen) + retStr + alphabet.substring(0, halfLen);
      final int excess = retStr.length - minHashLength;
      if (excess > 0) {
        final int startPos = excess ~/ 2;
        retStr = retStr.substring(startPos, startPos + minHashLength);
      }
    }

    return retStr;
  }

  List<int> _decode(String hash, String alphabet) {
    final List<int> ret = new List<int>();

    int i = 0;
    final RegExp regexp = new RegExp(r'[' + _guards + ']');
    String hashBreakdown = hash.replaceAll(regexp, ' ');
    List<String> hashArray = hashBreakdown.split(' ');

    if (hashArray.length == 3 || hashArray.length == 2) {
      i = 1;
    }

    if (hashArray.length > 0) {
      hashBreakdown = hashArray[i];
      if (hashBreakdown.isNotEmpty) {
        final String lottery = hashBreakdown[0];

        hashBreakdown = hashBreakdown.substring(1);
        hashBreakdown = hashBreakdown.replaceAll('[' + _seps + ']', ' ');
        hashArray = hashBreakdown.split(' ');

        String subHash, buffer;
        for (final String aHashArray in hashArray) {
          subHash = aHashArray;
          buffer = lottery + salt + alphabet;
          alphabet = Hashids.consistentShuffle(
              alphabet, buffer.substring(0, alphabet.length));
          ret.add(Hashids.unhash(subHash, alphabet));
        }
      }
    }

    if (encode(ret) != hash) {
      return <int>[];
    }

    return ret;
  }

  static String consistentShuffle(String alphabet, String salt) {
    if (salt.length <= 0) {
      return alphabet;
    }

    var integer, j, temp;

    for (var i = alphabet.length - 1, v = 0, p = 0; i > 0; i--, v++) {
      v %= salt.length;
      p += integer = salt[v].codeUnitAt(0);
      j = (integer + v + p) % i;

      temp = alphabet[j];
      alphabet =
          alphabet.substring(0, j) + alphabet[i] + alphabet.substring(j + 1);
      alphabet = alphabet.substring(0, i) + temp + alphabet.substring(i + 1);
    }

    return alphabet;
  }

  static String hash(int input, String alphabet) {
    String hash = '';
    final int alphabetLen = alphabet.length;

    do {
      final int index = input % alphabetLen;
      if (index >= 0 && index < alphabet.length) {
        hash = alphabet[index] + hash;
      }
      input ~/= alphabetLen;
    } while (input > 0);

    return hash;
  }

  static int unhash(String input, String alphabet) {
    int number = 0, pos;

    for (int i = 0; i < input.length; i++) {
      pos = alphabet.indexOf(input[i]);
      number = number * alphabet.length + pos;
    }

    return number;
  }
}
