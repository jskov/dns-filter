package dk.mada.dns.resolver;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Defines the upstream DNS service to use.
 */
public class UpsteamDnsServer {
	private static final String UPSTREAM_DNS_SERVER = "1.1.1.1";

	public static InetSocketAddress getActive() {
		try {
			return new InetSocketAddress(Inet4Address.getByName(UPSTREAM_DNS_SERVER), 53);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Bad upstream host " + UPSTREAM_DNS_SERVER, e);
		}
	}
}
