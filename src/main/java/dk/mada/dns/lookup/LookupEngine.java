package dk.mada.dns.lookup;

import java.util.Optional;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsReply;

public class LookupEngine {
	private final Resolver resolver;
	private final Blacklist blacklist;
	private final Whitelist whitelist;

	public LookupEngine(Resolver resolver, Blacklist blacklist, Whitelist whitelist) {
		this.resolver = resolver;
		this.blacklist = blacklist;
		this.whitelist = whitelist;
	}

	public LookupResult lookup(Query q) {
		var result = new LookupResult();
		
		String name = q.getRequestName();
		if (blacklist.test(name)) {
			q.setState(LookupState.BLACKLISTED);
			
		}
		
		Optional<DnsReply> reply = resolver.resolve(q.getClientIp(), q.getRequest());
		
		result.setReply(reply.orElse(null));
		
		return result;
	}

}
