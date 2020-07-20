package dk.mada.dns.lookup;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Deny;
import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.Allow;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsHeaderReply;
import dk.mada.dns.wire.model.DnsRecords;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsSectionAdditional;
import dk.mada.dns.wire.model.DnsSectionAnswer;

/**
 * Looks up query via upstream, applying allows, 
 * denies, and blocked list to the result.
 * 
 * The lists take priority like this:
 *  - allowed (locally defined)
 *  - denied (locally defined)
 *  - blocked list (externally defined)
 *  
 * But as an optimization, a denied query
 * will block without upstream lookup.
 * This means that allowed cannot override
 * in this case.
 */
public class LookupEngine {
	private static final Logger logger = LoggerFactory.getLogger(LookupEngine.class);
	
	private static final int BLOCKED_TTL_SECONDS = 60*3;
	
	private final Resolver resolver;
	private final Deny deny;
	private final Block block;
	private final Allow allow;

	public LookupEngine(Resolver resolver, Block block, Deny deny, Allow allow) {
		this.resolver = resolver;
		this.block = block;
		this.deny = deny;
		this.allow = allow;
	}

	public LookupResult lookup(Query q) {
		String name = q.getRequestName();
		
		if (deny.test(name) && !q.isDebugBypassRequest()) {
			return makeBlockedReply(q, LookupState.DENIED, name);
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

		String allowedName = intermediateNames.stream()
				.filter(allow::test)
				.findFirst()
				.orElse(null);
		
		if (allowedName != null) {
			return makeAllowedReply(q, reply.getHeader(), reply.getAnswer(), reply.getAdditional(), allowedName);
		}
		
		String deniedName = intermediateNames.stream()
				.filter(deny::test)
				.findFirst()
				.orElse(null);
		
		if (deniedName != null) {
			return makeBlockedReply(q, LookupState.DENIED, deniedName);
		}

		String blockedName = intermediateNames.stream()
				.filter(block::test)
				.findFirst()
				.orElse(null);
		
		if (blockedName != null) {
			return makeBlockedReply(q, LookupState.BLOCKED, blockedName);
		}

		return makePassthroughReply(q, reply.getHeader(), reply.getAnswer(), reply.getAdditional());
	}

	private LookupResult makePassthroughReply(Query q, DnsHeaderReply replyHeader, DnsSectionAnswer answer, DnsSectionAdditional additional) {
		var result = new LookupResult();
		logger.info("{} is passed through", q.getRequestName());
		result.setState(LookupState.PASSTHROUGH);

		var reply = DnsReplies.fromRequestWithAnswer(q.getRequest(), replyHeader, answer, additional);

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

	
	private LookupResult makeAllowedReply(Query q, DnsHeaderReply headerReply, DnsSectionAnswer answer, DnsSectionAdditional additional, String passedDueTo) {
		var result = new LookupResult();
		logger.info("{} is allowed due to {}", q.getRequestName(), passedDueTo);
		result.setState(LookupState.ALLOWED);

		var reply = DnsReplies.fromRequestWithAnswer(q.getRequest(), headerReply, answer, additional);

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
