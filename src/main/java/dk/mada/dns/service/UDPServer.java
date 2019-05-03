package dk.mada.dns.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.net.NetworkHelper;

/**
 * UDP server listening for clients on the designated port.
 * 
 * Requests are passed on to packet handlers for processing.
 * Their reply is sent back to the client.
 */
public class UDPServer {
	private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
	public static final int MIN_DNS_PACKET_SIZE = 512;

	private Executor executorService = Executors.newSingleThreadExecutor();
	private InetSocketAddress listenAddress;
	private UDPPacketHandler packetHandler;

	public UDPServer(int port) {
		listenAddress = NetworkHelper.makeLocalhostSocketAddress(port);
	}

	public void start() {
        executorService.execute(this::serve);
	}

	public void stop() {
		
	}

	private void serve() {
		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.bind(listenAddress);
			
			// FIXME: Does this bind to only the first connecting remote? RFC says to keep connection open for at least 2 minutes
			while (channel.isOpen()) {
				ByteBuffer request = ByteBuffer.allocate(MIN_DNS_PACKET_SIZE);
				SocketAddress sa = channel.receive(request);
				request.flip();
				
				ByteBuffer reply = packetHandler.process(request);
				if (channel.isConnected()) {
					channel.write(reply);
				} else {
					channel.send(reply, sa);
				}
			}
		} catch (IOException e) {
			logger.error("UDP Server failed", e);
		}
	}

	public void setPacketHandler(UDPPacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}
}
