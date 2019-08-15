package dk.mada.dns.net;

import java.net.InetSocketAddress;

/**
 * Networking helper methods.
 */
public class NetworkHelper {
	private NetworkHelper() {}
	
	public static InetSocketAddress makeLocalhostSocketAddress(int port) {
		try {
			// InetAddress.getByAddress(new byte[] { 127,0,0,1 })
			return new InetSocketAddress("localhost", port);
		} catch (Exception e) {
			throw new IllegalStateException("Bad host", e);
		}
	}

}
