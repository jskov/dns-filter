# Installation

## Prepare data folder

	$ sudo mkdir -p /opt/data/services/dns-filter/
	$ sudo chown dns.dns /opt/data/services/dns-filter/

## Prepare/change image hash

	$ sudo mkdir -p /etc/sysconfig/mada/
	$ sudo chown jskov.jskov /etc/sysconfig/mada
	# This command echos the version into `/etc/sysconfig/mada/dns-filter.hash` and restarts the dns-filter service
	$ dns-filter-push sha256:966e8ac70845e2b47dc623f4d2d3c6953b6db6897dd32e79a2cf7a81a3d13cee

## Setup service

	$ sudo cp modules/container/etc/systemd/dns-filter.service /etc/systemd/system/
	$ sudo systemctl daemon-reload
	$ sudo start dns-filter


## Show log

	$ journalctl -f -e CONTAINER_TAG=dns-filter

