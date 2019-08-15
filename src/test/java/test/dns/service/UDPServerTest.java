package test.dns.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.service.UDPPacketHandler;
import dk.mada.dns.service.UDPServer;
import fixture.datagram.DatagramHelper;

/**
 * Tests reception and dispatching of UDP packets.
 */
public class UDPServerTest {
	private static final Logger logger = LoggerFactory.getLogger(UDPServerTest.class);
	private static final int PORT = 2053;

	@BeforeAll
	static void info() throws UnknownHostException {
    	InetAddress lh = InetAddress.getLocalHost();
    	logger.info("HOST {} : {} : {}", lh.getHostName(), lh.getHostAddress(),  InetAddress.getLoopbackAddress());
	}

	/**
	 * Tests that the UDP listening service can be orderly
	 * shut down and releases the port.
	 */
	@Test
	public void serverCanBeStopped() {
		UDPServer sut = new UDPServer(PORT);

		// Startup test
		sut.start();
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
