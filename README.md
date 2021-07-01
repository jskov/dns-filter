![](https://github.com/jskov/dns-filter/workflows/Build%20and%20run%20tests/badge.svg)


# dns-filter
DNS server with filtering of ad hosts/domains

Personal development project, not fit for general use.

# Building

## Java Uber

./gradlew quarkusBuild --uber-jar


Build in container:

	VERSION=0.0.1 ./build.sh -t x

## Native

Install graal 19.3, make sure it is on PATH.

	$ gu install native-image

# Hooking into Linux (Fedora 34+)

This is how to (temporarily) hack/hook up the dns-filter process in Fedora so it gets used for DNS requests.

## Run dns-filter from gradle

	$ ./gradlew :quarkusDev

Runs dns-filter on port 8053.

To trigger recompile/deploy, run:

	$ curl localhost:8080 > /dev/null

## Run dns-filter from a service

TBD

## Access on localhost via Resolved

Make resolved use dns-filter as its upstream server.

In `/etc/systemdresolved.conf` insert (in place of any existing DNS-line):

	DNS=127.0.0.1:8053

Then 

	sudo systemctl restart systemd-resolved


Disable again by reverting to the old state of the file.


## Access from other hosts on network

Make firewall redirect external 53 to 8053.

Note that this is removed at reset.

	sudo firewall-cmd --add-forward-port=port=53:proto=udp:toport=8053


## Running as root

Run the service using port 53 requires it to be started as root.

	$ DNS_FILTER_PORT_DNS=53 sudo /opt/tools/jdk-13/bin/java -jar dns-filter-$version-runner.jar

After creating the socket on port 53, it will drop its privileges and continue as user 65534 (or what is specified in environment variable DNS_FILTER_RUN_AS)

This should not really be necessary, given the firewall redirection stuff.

# Data capture for development

Capture binary data from a query by one of the below methods.

## Echo query

Make a dig query to hostname prefixed with `dns-echo.` like this:

	$ dig @localhost -p 8053 dns-echo.cnn.com

Then the following query of `cnn.com` will echo both query, nameserver reply, and filtered reply data.

## Bypass query

Make a dig query to hostname prefixed with `dns-bypass.` like this:

	$ dig @localhost -p 8053 dns-bypass.cnn.com

Then the following query of `cnn.com` will echo both query, and the nameserver reply. The reply will not be filtered.

# Vaadin

http://localhost:8080/vaadin/

https://vaadin.com/components/vaadin-text-field/java-examples/text-field

## Quarkus-vaadin-lab

From https://github.com/moewes/quarkus-vaadin-lab

Start as normal.

But no control gui rendered.

Fixed by adding com.vaadin:flow-server-compatibility-mode dependency.


## Nano-Vaadin-Demos

From https://github.com/Nano-Vaadin-Demos/nano-vaadin-quarkus

Start with:

	$ ./gradlew quarkusDev --jvm-args="-Dvaadin-pkg-to-scan=dk.mada.dns.web.app"
	
Created more recently, but seems simple (less complete?)

# Javafx client

## Run in Eclipse

* Check out. Run './gradlew eclipse' to set up for development in Eclipse.

To run in Eclipse, add VM arguments: `--add-modules ALL-MODULE-PATH`

