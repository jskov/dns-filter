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
	private static final int CONTINUATION_USER = 1000;
	private static final int PORT = 10053;

	/**
	 * Tests that the UDP listening service can be orderly
	 * shut down and releases the port.
	 */
	@Test
	public void serverCanBeStopped() {
		UDPServer sut = new UDPServer(PORT, CONTINUATION_USER);

		// Startup test
		sut.startAndDropPrivileges();
		assertThat(sut.isRunning())
			.isTrue();

		// Shutdown test
		sut.stop();
		assertThat(sut.isRunning())
			.isFalse();
	}
	
	/**
	 * Simple round trip test.
	 */
	@Test
	public void canRoundTripUdpPacket() throws IOException {
		UDPServer sut = new UDPServer(PORT, CONTINUATION_USER);

		try {
			UDPPacketHandler echoHandler = (clientIp, input) -> input;
			sut.setPacketHandler(echoHandler);
			sut.startAndDropPrivileges();

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
