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

# Hooking into Linux (Fedora)

This is how to (temporarily) hack/hook up the dns-filter process in Fedora so it gets used for DNS requests.

## Running as root

Run the service using port 53 requires it to be started as root.

	$ DNS_FILTER_PORT_DNS=53 sudo /opt/tools/jdk-13/bin/java -jar dns-filter-$version-runner.jar

After creating the socket on port 53, it will drop its privileges and continue as user 65534 (or what is specified in environment variable DNS_FILTER_RUN_AS)

## Via DnsMasq

The service can also run on unprivileged ports (does not require root), but then you must redirect the DNS traffic to the service.

This is how I have configured it to work on Fedora 31.

## Stop libvirt

Kill libvirt, which also runs dnsmasq.

	$ sudo systemctl stop libvirtd.service

And make sure no lingering dnsmasq process is running.

### Setup dnsmasq

Setup dnsmasq to pass requests first to the dns-filter, then fallback to an external server in case of failure

In `/etc/resolv-dnsmasq.conf`:

	# Specify timeout for dnsmasq requests                                                                                                      
	options timeout:1

In `/etc/dnsmasq.conf` add configuration:

	resolv-file=/etc/resolv-dnsmasq.conf
	strict-order
	no-resolv
	no-poll
	server=1.1.1.1
	server=127.0.0.1#8053 
 
## Switch resolve to localhost

Edit `/etc/resolv.conf`. Comment out current nameserver and add:

	nameserver 127.0.0.1

Revert to old setting to disable use of dns-filter.


## Run dns-filter from gradle

	$ ./gradlew :quarkusDev

Runs dns-filter on port 8053.

If you need to restart the process, dnsmasq may take some time to detect it. Get its attention with a restart:

	$ sudo systemctl restart dnsmasq


To trigger recompile/deploy, run:

	$ curl localhost:8080 > /dev/null

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

