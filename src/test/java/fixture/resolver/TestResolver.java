package fixture.resolver;

import java.util.Optional;

import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;

public class TestResolver implements Resolver {
	private int callCount;
	private DnsReply firstReply;

	public TestResolver() {
	}
	
	public TestResolver(DnsReply firstReply) {
		this.firstReply = firstReply;
	}
	
	@Override
	public Optional<DnsReply> resolve(String clientIp, DnsRequest request) {
		callCount++;
		return Optional.ofNullable(firstReply);
	}
	
	public boolean hasBeenCalled() {
		return callCount != 0;
	}
}
