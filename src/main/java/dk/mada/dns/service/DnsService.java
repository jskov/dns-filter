package dk.mada.dns.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

@ApplicationScoped
public class DnsService {
	private static final Logger logger = LoggerFactory.getLogger(DnsService.class);
	private static final int PORT = 8053;
	public static final int MIN_DNS_PACKET_SIZE = 512;

	private Executor dnsExecutorService = Executors.newFixedThreadPool(5);

	public void run() {
		InetSocketAddress listenAddress = makeAddress();
		logger.info("Starting DNS server, listening on port {}", listenAddress);

		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.bind(listenAddress);
			
			// FIXME: Does this bind to only the first connecting remote? RFC says to keep connection open for at least 2 minutes
			while (channel.isOpen()) {
				// FIXME: bind buffer per thread
				ByteBuffer buffer = ByteBuffer.allocate(MIN_DNS_PACKET_SIZE);
				
				SocketAddress sa = channel.receive(buffer);
				dnsExecutorService.execute(() -> process(channel, sa, buffer));
			}
		} catch (AsynchronousCloseException ace) {
			logger.trace("server shutting down", ace);
		} catch (IOException e) {
			logger.error("Failed to connect to port {}", PORT, e);
		} finally {
			logger.info("Closing DNS server");
		}
	}
	
	public void process(DatagramChannel channel, SocketAddress sa, ByteBuffer request) {
		try {
			logger.debug("Connected to {} with size {}", sa.toString(), request.position());
			
			request.flip();
			
			ByteBuffer replyBb;
			/*
			try {
				if (channel.isConnected()) {
					channel.write(replyBb);
				} else {
					channel.send(replyBb, sa);
				}
			} catch (IOException e) {
				logger.warn("Failed to send reply", e);
			}
			*/
		} finally {}
	}
	
	private void lookup() throws TextParseException, UnknownHostException {
    	Lookup lookup = new Lookup("google.com");
    	SimpleResolver localhostResolver = new SimpleResolver("10.0.0.10");
    	localhostResolver.setPort(53);
		lookup.setResolver(localhostResolver);
    	lookup.setCache(null);

    	Record[] records = lookup.run();
    	if (records == null) {
    		throw new IllegalStateException("Failed to lookup");
    	}
    	Record r = null;
	}
	
	private InetSocketAddress makeAddress() {
		try {
			return new InetSocketAddress(InetAddress.getByAddress(new byte[] { 127,0,0,1 }), PORT);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Bad host", e);
		}
	}

}
