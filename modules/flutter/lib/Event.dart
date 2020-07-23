import 'dart:developer' as dev;

enum LookupType { PASSTHROUGH }

class Event {
  final String hostname;
  final String ip;
  final LookupType type;

  Event({this.hostname, this.ip, this.type});

  String toString() {
    String action;
    switch (type) {
      case LookupType.PASSTHROUGH:
        action = ">";
        break;
      default:
        action = "?";
    }

    return "$action $hostname : $ip";
  }

  factory Event.fromJson(Map<String, dynamic> json) {
    return Event(
      hostname: json['hostname'] as String,
      ip: json['ip'] as String,
      type: typeFromJson(json['type'] as String),
    );
  }

  static LookupType typeFromJson(String name) {
    String find = "LookupType.$name";
    for (var v in LookupType.values) {
      dev.log('type ${v.toString()} $find', name: 'enum.deser');

      if (v.toString() == find) {
        return v;
      }
    }
    throw FormatException("Unknown lookup type '$name'");
  }
}
