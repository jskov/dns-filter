package fixture.resolver;

import java.util.Optional;

import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;

/**
 * Resolver replying a canned reply.
 */
public class CannedModelResolver implements Resolver {
	private int callCount;
	private DnsReply firstReply;

	public CannedModelResolver() {
	}
	
	public CannedModelResolver(DnsReply firstReply) {
		this.firstReply = firstReply;
	}
	
	@Override
	public Optional<DnsReply> resolve(String clientIp, DnsRequest request) {
		callCount++;
		if (callCount == 2) {
			throw new IllegalStateException("Should only be called once!");
		}
		return Optional.ofNullable(firstReply);
	}
	
	public boolean hasBeenCalled() {
		return callCount != 0;
	}
}
