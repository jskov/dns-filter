![](https://github.com/jskov/dns-filter/workflows/Build%20and%20test/badge.svg)
![](https://github.com/jskov/dns-filter/.github/workflows/run-tests.yml/badge.svg)
# dns-filter
DNS server with filtering of ad hosts/domains


# Maven

./mvnw surefire-report:report

./mvnw compile quarkus:dev

Trigger rebuild with

curl localhost:8080 > /dev/null

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
