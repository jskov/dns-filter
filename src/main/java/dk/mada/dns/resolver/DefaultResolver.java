package dk.mada.dns.resolver;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.mada.dns.resolver.external.ExternalDnsGateway;
import dk.mada.dns.service.DevelopmentDebugging;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;

/**
 * Simple DNS resolver, asking upstream for a lookup.
 * Both input and output are model based.
 */
@ApplicationScoped
public class DefaultResolver implements Resolver {
	private UdpNameServer nameServer;
	
	DefaultResolver() {} // needed by quarkus arc?!
	
	@Inject
	public DefaultResolver(UdpNameServer nameServer) {
		this.nameServer = nameServer;
	}
	
	public static DefaultResolver externalDebugResolver() {
		ExternalDnsGateway dnsGateway = new ExternalDnsGateway(DevelopmentDebugging.echoAllRequests());
		return new DefaultResolver(dnsGateway);
	}
	
	@Override
	public Optional<DnsReply> resolve(String clientIp, DnsRequest request) {
		Objects.requireNonNull(clientIp);
		Objects.requireNonNull(request);

		String hostname = request.getQuestion().getName().getName();

		ByteBuffer query = request.asWirePacket();

		return nameServer.lookup(hostname, query)
				.map(DnsReplies::fromWireData);
	}
}
