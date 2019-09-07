package dk.mada.dns.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.RRset;

import dk.mada.dns.websocket.DnsQueryEventService;
import dk.mada.dns.websocket.dto.DnsQueryEventDto;
import dk.mada.dns.websocket.dto.EventTypeDto;

/**
 * DNS lookup, passing request on to upstream DNS server (pass-through).
 * This is a very crude implementation, based on org.xbill.DNS, just to
 * test that the event system works.
 */
@ApplicationScoped
public class DnsLookupService implements UDPPacketHandler {
	private static final Logger logger = LoggerFactory.getLogger(DnsLookupService.class);
	private static final String UPSTREAM_DNS_SERVER = "1.1.1.1";

	@Inject private DnsQueryEventService websocketEventNotifier;
	
	@Override
	public ByteBuffer process(String clientIp, ByteBuffer request) {
		Objects.requireNonNull(clientIp);
		Objects.requireNonNull(request);

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

				notifyEventListeners(r);
				
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
	private void notifyEventListeners(Message reply) {
		DnsQueryEventDto dto = new DnsQueryEventDto();
		dto.hostname = reply.getQuestion().getName().toString();
		
		RRset firstRecord = reply.getSectionRRsets(1)[0];
		dto.ttl = firstRecord.getTTL();
		for (var ix = firstRecord.rrs(); ix.hasNext();) {
			Object o = ix.next();
			if (o instanceof ARecord) {
				dto.ip = ((ARecord)o).getAddress().getHostAddress();
			}
		}
		dto.type = EventTypeDto.PASSTHROUGH;
		
		websocketEventNotifier.broadcast(dto);
	}
	
	private static InetSocketAddress getUpstreamServer() {
		try {
			return new InetSocketAddress(Inet4Address.getByName(UPSTREAM_DNS_SERVER), 53);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Bad upstream host " + UPSTREAM_DNS_SERVER, e);
		}
	}
}
