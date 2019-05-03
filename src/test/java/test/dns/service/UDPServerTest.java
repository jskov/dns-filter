package test.dns.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.junit.jupiter.api.Test;

import dk.mada.dns.service.UDPServer;
import dk.mada.dns.service.UDPPacketHandler;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests reception and dispatching of UDP packets.
 */
public class UDPServerTest {
	private static final int PORT = 1053;

	@Test
	public void canAcceptUdpPacket() throws IOException {
		UDPServer sut = new UDPServer(PORT);

		try {
			UDPPacketHandler echoHandler = input -> input;
			sut.setPacketHandler(echoHandler);
			sut.start();

			ByteBuffer input = ByteBuffer.wrap("foobar".getBytes());
			ByteBuffer reply = sendBufferToLocalUDPPort(input);

			input.flip();
			
			assertThat(input)
				.isEqualTo(reply);
		} finally {
			sut.stop();
		}

	}

	private ByteBuffer sendBufferToLocalUDPPort(ByteBuffer input) throws IOException {
		InetSocketAddress target = getUpstreamServer();
		
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
		
	private static InetSocketAddress getUpstreamServer() {
		try {
			return new InetSocketAddress(Inet4Address.getByName("localhost"), PORT);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Bad upstream host", e);
		}
	}

	
}
