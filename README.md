![](https://github.com/jskov/dns-filter/workflows/Build%20and%20test/badge.svg)
![](https://github.com/jskov/dns-filter/.github/workflows/run-tests.yml/badge.svg)
# dns-filter
DNS server with filtering of ad hosts/domains

Personal development project, not fit for general use.


# Hooking into Linux (Fedora)

This is how to (temporarily) hack/hook up the dns-filter process in Fedora so it gets used for DNS requests.

## Stop libvirt

Kill libvirt, which also runs dnsmasq.

	$ sudo systemctl stop libvirtd.service

And make sure no lingering dnsmasq process is running.

## Setup dnsmasq

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


## Run dns-filter

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

