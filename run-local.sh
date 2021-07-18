#!/bin/bash

./gradlew quarkusBuild

rm -rf /tmp/quarkus-app
cp -a build/quarkus-app /tmp/

cd /tmp/quarkus-app
QUARKUS_HTTP_PORT=9080 DNS_FILTER_PORT_DNS=9053 java -jar quarkus-run.jar

