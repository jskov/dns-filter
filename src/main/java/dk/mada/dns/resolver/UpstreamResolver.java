package dk.mada.dns.resolver;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;

/**
 * Simple DNS resolver, asking upstream for a lookup.
 * Both input and output are model based.
 */
@ApplicationScoped
public class UpstreamResolver implements Resolver {
	private static final Logger logger = LoggerFactory.getLogger(UpstreamResolver.class);

	@Inject private ExternalDnsGateway dnsGateway;
	
	@Override
	public Optional<DnsReply> resolve(String clientIp, DnsRequest request) {
		Objects.requireNonNull(clientIp);
		Objects.requireNonNull(request);

		String lookupHost = request.getQuestion().getName().getName();

		logger.info("Contact upstream server");

		ByteBuffer bb = request.asWirePacket();

		return dnsGateway.passOnToUpstreamServer(lookupHost, bb)
				.map(DnsReplies::fromWireData);
	}
}
