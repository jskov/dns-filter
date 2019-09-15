package fixture.resolver;

import java.util.Optional;

import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;

public class TestResolver implements Resolver {
	private int callCount;
	
	@Override
	public Optional<DnsReply> resolve(String clientIp, DnsRequest request) {
		callCount++;
		return Optional.empty();
	}
	
	public boolean hasBeenCalled() {
		return callCount != 0;
	}
}
