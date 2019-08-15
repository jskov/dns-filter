package dk.mada.dns.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Message;

/**
 * DNS lookup, passing request on to upstream DNS server (pass-through).
 */
public class DnsLookup implements UDPPacketHandler {
	private static final Logger logger = LoggerFactory.getLogger(DnsLookup.class);
	private static final String UPSTREAM_DNS_SERVER = "1.1.1.1";

	@Override
	public ByteBuffer process(ByteBuffer request) {
		try {
			
			Message m = new Message(request);
			logger.info("LOOKUP {}", m);
			
			InetSocketAddress target = getUpstreamServer();
			try (DatagramChannel channel = DatagramChannel.open()) {
				channel.connect(target);
				request.rewind();
				channel.send(request, target);
		
				ByteBuffer reply = ByteBuffer.allocate(512);
		
				long start = System.currentTimeMillis();

				channel.read(reply);
				reply.flip();
		
				long time = System.currentTimeMillis() - start;
				logger.info("Upstream reply in {}ms", time);

				Message r = new Message(reply);
				logger.info("REPLY {}", r);
				reply.rewind();

				return reply;
			} catch (ClosedByInterruptException e) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e1) {
					logger.debug("Cleared interrupted state", e1);
				}
				logger.info("Shut down by interrupt");
				throw e;
			} catch (Exception e) {
				logger.warn("Failed", e);
			}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return request;
	}
	
	private static InetSocketAddress getUpstreamServer() {
		try {
			return new InetSocketAddress(Inet4Address.getByName(UPSTREAM_DNS_SERVER), 53);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Bad upstream host " + UPSTREAM_DNS_SERVER, e);
		}
	}
}
