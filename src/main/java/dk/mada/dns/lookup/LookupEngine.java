package dk.mada.dns.lookup;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;

public class LookupEngine {
	private static final Logger logger = LoggerFactory.getLogger(LookupEngine.class);
	
	private static final int BLOCKED_TTL_SECONDS = 60*3;
	
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
		
		logger.info("Look up {}", name);
		
		if (blacklist.test(name)) {
			logger.info(" {} is blacklisted", name);
			result.setState(LookupState.BLACKLISTED);
			result.setReply(makeBlockedReply(q));
			return result;
		}
		
		Optional<DnsReply> reply = resolver.resolve(q.getClientIp(), q.getRequest());
		
		logger.info("Got resolved {}", reply);
		
		
		result.setReply(reply.orElse(null));
		
		return result;
	}

	
	private DnsReply makeBlockedReply(Query q) {
		
		var name = q.getRequest().getQuestion().getName();
		var deadend = DnsRecordA.blindFrom(name, BLOCKED_TTL_SECONDS);
		
		return DnsReplies.fromRequestWithAnswer(q.getRequest(), deadend);
	}
}
