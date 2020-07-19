package fixture.datagram;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import dk.mada.dns.net.NetworkHelper;

/**
 * Datagram helpers.
 */
public class DatagramHelper {

	/**
	 * Sends buffer to localhost port, returns reply.
	 * 
	 * @param input buffer to send to port via UDP
	 * @return reply from service.
	 * @throws IOException on failure
	 */
	public static ByteBuffer sendBufferToLocalUDPPort(int port, ByteBuffer input) throws IOException {
		InetSocketAddress target = NetworkHelper.makeLocalhostSocketAddress(port);
		
		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.connect(target);
			input.rewind();
			channel.send(input, target);
	
			ByteBuffer reply = ByteBuffer.allocate(512);
			channel.read(reply);
			reply.flip();
			
			return reply;
		}
	}
}
