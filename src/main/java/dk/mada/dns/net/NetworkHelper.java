package dk.mada.dns.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Networking helper methods.
 */
public class NetworkHelper {
	private NetworkHelper() {}
	
	public static InetSocketAddress makeLocalhostSocketAddress(int port) {
		try {
			return new InetSocketAddress(InetAddress.getByAddress(new byte[] { 127,0,0,1 }), port);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Bad host", e);
		}
	}

}
