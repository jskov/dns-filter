# test_flutter

A new Flutter project.

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://flutter.dev/docs/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://flutter.dev/docs/cookbook)

For help getting started with Flutter, view our
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.


## vscode

Install flutter and dart extensions.

```console
$ android
$ sdkmanager "system-images;android-28;default;x86_64"
$ avdmanager create avd --name AndroidDevice01 --package "system-images;android-28;default;x86_64"
Auto-selecting single ABI x86_64========] 100% Fetch remote repository...
$ emulator -avd AndroidDevice01



$ flutter pub global activate devtools
````



ctrl-shif-p flutter: launch emulator

F5 to run app on emulator

### Networking 

https://flutter.dev/docs/cookbook#networking


This works:

curl 'http://localhost:8080/chat/client-006' -H 'Sec-WebSocket-Version: 13' -H 'Sec-WebSocket-Key: OFZ2hu5AYodja92EHvaL6Q==' -H 'Upgrade: websocket'

A simple curl on that endpoint results in 404. The headers are important.
And they are not included by IOWebSocketChannel.
