package dk.mada.dns.lookup;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Blockedlist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsRecords;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsSectionAdditional;
import dk.mada.dns.wire.model.DnsSectionAnswer;

/**
 * Looks up query via upstream, applying white list, 
 * black list and blocked list to the result.
 * 
 * The lists take priority like this:
 *  - white list (locally defined)
 *  - black list (locally defined)
 *  - blocked list (externally defined)
 *  
 * But as an optimization, a black listed query
 * will block without upstream lookup.
 * This means that the white list cannot override
 * in this case.
 */
public class LookupEngine {
	private static final Logger logger = LoggerFactory.getLogger(LookupEngine.class);
	
	private static final int BLOCKED_TTL_SECONDS = 60*3;
	
	private final Resolver resolver;
	private final Blacklist blacklist;
	private final Blockedlist blockedlist;
	private final Whitelist whitelist;

	public LookupEngine(Resolver resolver, Blockedlist blockedlist, Blacklist blacklist, Whitelist whitelist) {
		this.resolver = resolver;
		this.blockedlist = blockedlist;
		this.blacklist = blacklist;
		this.whitelist = whitelist;
	}

	public LookupResult lookup(Query q) {
		String name = q.getRequestName();
		
		if (blacklist.test(name) && !q.isDebugBypassRequest()) {
			return makeBlockedReply(q, LookupState.BLACKLISTED, name);
		}

		logger.debug("Look up {}", name);
		
		DnsReply reply = resolver.resolve(q.getClientIp(), q.getRequest())
				.orElse(null);

		logger.debug("Got resolved {}", reply);

		if (reply == null) {
			var result = new LookupResult();
			result.setState(LookupState.FAILED);
			return result;
		}
		
		if (q.isDebugBypassRequest()) {
			return makeBypassReply(q, reply);
		}
		
		List<String> intermediateNames = reply.getAnswer().getRecords().stream()
			.map(r -> r.getName())
			.map(n -> n.getName())
			.collect(toList());

		String whitelistedName = intermediateNames.stream()
				.filter(whitelist::test)
				.findFirst()
				.orElse(null);
		
		if (whitelistedName != null) {
			return makeWhitelistReply(q, reply.getAnswer(), reply.getAdditional(), whitelistedName);
		}
		
		String blacklistedName = intermediateNames.stream()
				.filter(blacklist::test)
				.findFirst()
				.orElse(null);
		
		if (blacklistedName != null) {
			return makeBlockedReply(q, LookupState.BLACKLISTED, blacklistedName);
		}

		String blockedName = intermediateNames.stream()
				.filter(blockedlist::test)
				.findFirst()
				.orElse(null);
		
		if (blockedName != null) {
			return makeBlockedReply(q, LookupState.BLOCKED, blockedName);
		}

		return makePassthroughReply(q, reply.getAnswer(), reply.getAdditional());
	}

	private LookupResult makePassthroughReply(Query q, DnsSectionAnswer answer, DnsSectionAdditional additional) {
		var result = new LookupResult();
		logger.info("{} is passed through", q.getRequestName());
		result.setState(LookupState.PASSTHROUGH);

		logger.info("Additionals: {}", additional);
		
		var reply = DnsReplies.fromRequestWithAnswer(q.getRequest(), answer, additional);

		result.setReply(reply);
		return result;
	}

	// Bypass does not go wire->model->wire
	private LookupResult makeBypassReply(Query q, DnsReply reply) {
		var result = new LookupResult();
		logger.info("{} is bypassed", q.getRequestName());
		result.setState(LookupState.BYPASS);

		result.setReply(reply);
		return result;
	}

	
	private LookupResult makeWhitelistReply(Query q, DnsSectionAnswer answer, DnsSectionAdditional additional, String passedDueTo) {
		var result = new LookupResult();
		logger.info("{} is whitelisted due to {}", q.getRequestName(), passedDueTo);
		result.setState(LookupState.WHITELISTED);

		var reply = DnsReplies.fromRequestWithAnswer(q.getRequest(), answer, additional);

		result.setReply(reply);
		return result;
	}

	public LookupResult makeBlockedReply(Query q, LookupState state, String blockedDueTo) {
		var result = new LookupResult();
		logger.info("{} is blocked due to {}", q.getRequestName(), blockedDueTo);
		result.setState(state);

		var name = q.getRequest().getQuestion().getName();
		var deadend = DnsRecords.aRecordBlindFrom(name, BLOCKED_TTL_SECONDS);
		
		var reply = DnsReplies.fromRequestToBlockedReply(q.getRequest(), deadend);

		result.setReply(reply);
		return result;
	}
}
