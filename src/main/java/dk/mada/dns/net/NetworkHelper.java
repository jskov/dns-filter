package dk.mada.dns.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Networking helper methods.
 */
public class NetworkHelper {
	private NetworkHelper() {}
	
	public static InetSocketAddress makeLocalhostSocketAddress(int port) {
		return new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
	}
	
	public static InetSocketAddress makePublicAddress(int port) {
		return new InetSocketAddress(port);
	}
}
