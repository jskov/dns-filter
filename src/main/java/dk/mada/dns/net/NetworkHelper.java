package dk.mada.dns.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Networking helper methods.
 */
public class NetworkHelper {
	private NetworkHelper() {}
	
	public static InetSocketAddress makeLocalhostSocketAddress(int port) {
		try {
			return new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
		} catch (Exception e) {
			throw new IllegalStateException("Bad host", e);
		}
	}

}
