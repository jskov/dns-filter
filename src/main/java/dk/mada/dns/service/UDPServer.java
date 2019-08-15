package dk.mada.dns.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
		executorService.shutdownNow();
		try {
			executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Interrupted while waiting for shutdown", e);
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	private void serve() {
		running.set(true);
		startupLatch.countDown();
		
		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.bind(listenAddress);
			
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
		} catch (ClosedByInterruptException e) {
			logger.debug("UDP server stopped", e);
		} catch (IOException e) {
			logger.error("UDP Server failed", e);
		} finally {
			running.set(false);
		}
	}

	public void setPacketHandler(UDPPacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}
}
