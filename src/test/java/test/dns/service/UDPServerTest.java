package test.dns.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import dk.mada.dns.service.UDPPacketHandler;
import dk.mada.dns.service.UDPServer;
import fixture.datagram.DatagramHelper;

/**
 * Tests reception and dispatching of UDP packets.
 */
public class UDPServerTest {
	private static final int PORT = 1053;

	/**
	 * Simple round trip test.
	 */
	@Test
	public void canRoundTripUdpPacket() throws IOException {
		UDPServer sut = new UDPServer(PORT);

		try {
			UDPPacketHandler echoHandler = input -> input;
			sut.setPacketHandler(echoHandler);
			sut.start();

			ByteBuffer input = ByteBuffer.wrap("foobar".getBytes());
			ByteBuffer reply = DatagramHelper.sendBufferToLocalUDPPort(PORT, input);

			input.flip();
			
			assertThat(input)
				.isEqualTo(reply);
		} finally {
			sut.stop();
		}
	}
}
