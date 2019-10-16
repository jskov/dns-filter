package dk.mada.dns.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.net.NetworkHelper;
import dk.mada.dns.util.Hexer;

/**
 * UDP server listening for clients on the designated port.
 * 
 * Requests are passed on to packet handlers for processing.
 * Their reply is sent back to the client.
 */

public class UDPServer {
	private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
	public static final int MIN_DNS_PACKET_SIZE = 512;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private InetSocketAddress listenAddress;
	private UDPPacketHandler packetHandler;

	private AtomicBoolean running = new AtomicBoolean();
	private CountDownLatch startupLatch = new CountDownLatch(1);
	
	public UDPServer(int port) {
		listenAddress = NetworkHelper.makeLocalhostSocketAddress(port);
		logger.info("Created service on {}", listenAddress);
	}

	public void start() {
        executorService.execute(this::serve);
        try {
			startupLatch.await();
		} catch (InterruptedException e) {
			throw new IllegalStateException("Interrupted while waiting for startup", e);
		}
	}

	public void stop() {
		logger.info("Telling UDP server to shut down");
		executorService.shutdownNow();
		try {
			executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Interrupted while waiting for shutdown", e);
		}
		logger.info("UDP server has terminated");
	}

	public boolean isRunning() {
		return running.get();
	}

	private void serve() {
		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.bind(listenAddress);
			
			running.set(true);
			startupLatch.countDown();
			
			while (channel.isOpen()) {
				ByteBuffer request = ByteBuffer.allocate(MIN_DNS_PACKET_SIZE);
				SocketAddress sa = channel.receive(request);
				request.flip();

				if (!(sa instanceof InetSocketAddress)) {
					logger.warn("Does not know IP of client - ignoring");
					continue;
				}
				
				String clientIp = ((InetSocketAddress)sa).getAddress().getHostAddress();
				if (clientIp == null) {
					continue;
				}
				
				try {
				ByteBuffer reply = packetHandler.process(clientIp, request);
				if (channel.isConnected()) {
					channel.write(reply);
				} else {
					channel.send(reply, sa);
				}
				} catch (Exception e) {
					request.rewind();
					logger.warn("FIXME: bad exception handling, Failed to lookup", e);
					Hexer.printForDevelopment("Bad handling: " + e.getMessage(), request, Collections.emptySet());
				}
			}
		} catch (ClosedByInterruptException e) {
			logger.debug("UDP server stopped", e);
		} catch (IOException e) {
			logger.error("UDP Server failed", e);
		} finally {
			running.set(false);
			startupLatch.countDown();
		}
	}

	public void setPacketHandler(UDPPacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}
}
