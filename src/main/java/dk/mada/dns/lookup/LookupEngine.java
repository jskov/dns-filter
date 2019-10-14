package dk.mada.dns.lookup;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsRecords;
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
		String name = q.getRequestName();
		
		logger.info("Look up {}", name);
		
		if (blacklist.test(name)) {
			return makeBlockedReply(q, name);
		}
		
		var result = new LookupResult();
		DnsReply reply = resolver.resolve(q.getClientIp(), q.getRequest())
				.orElse(null);

		logger.info("Got resolved {}", reply);

		if (reply == null) {
			result.setState(LookupState.FAILED);
			return result;
		}

		List<String> intermediateNames = reply.getAnswer().getRecords().stream()
			.map(r -> r.getName())
			.map(n -> n.getName())
			.collect(toList());
		
		String blacklistedName = intermediateNames.stream()
				.filter(blacklist::test)
				.findFirst()
				.orElse(null);
		
		if (blacklistedName != null) {
			return makeBlockedReply(q, blacklistedName);
		}
		
		
		return result;
	}

	private LookupResult makeBlockedReply(Query q, String blockedDueTo) {
		var result = new LookupResult();
		logger.info(" {} is blacklisted", blockedDueTo);
		result.setState(LookupState.BLACKLISTED);

		var name = q.getRequest().getQuestion().getName();
		var deadend = DnsRecords.aRecordBlindFrom(name, BLOCKED_TTL_SECONDS);
		
		var reply = DnsReplies.fromRequestWithAnswer(q.getRequest(), deadend);

		result.setReply(reply);
		return result;
	}
}
