package dk.mada.dns.resolver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.conversion.WireToModelXbill;

/**
 * Simple DNS resolver, asking upstream for a lookup.
 * Both input and output are model based.
 */
@ApplicationScoped
public class DnsResolver {
	private static final Logger logger = LoggerFactory.getLogger(DnsResolver.class);

	private WireToModelXbill wireToModelConverter = new WireToModelXbill();
	
	public Optional<DnsReply> resolve(String clientIp, DnsRequest request) {
		Objects.requireNonNull(clientIp);
		Objects.requireNonNull(request);

		try {
			InetSocketAddress target = UpsteamDnsServer.getActive();
			try (DatagramChannel channel = DatagramChannel.open()) {
				channel.connect(target);
				channel.send(request.asWirePacket(), target);
		
				ByteBuffer reply = ByteBuffer.allocate(512);
		
				long start = System.currentTimeMillis();

				channel.read(reply);
				reply.flip();
		
				long time = System.currentTimeMillis() - start;
				logger.info("Upstream reply in {}ms", time);

				return Optional.of(wireToModelConverter.replyToModel(reply));
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
			logger.warn("Failed to lookup", e);
		}
		return Optional.empty();
	}
}
