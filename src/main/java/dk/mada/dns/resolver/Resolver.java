package dk.mada.dns.resolver;

import java.util.Optional;

import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;

public interface Resolver {
	Optional<DnsReply> resolve(String clientIp, DnsRequest request);
}