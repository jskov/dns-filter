package dk.mada.dns.service;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.lookup.FilteredLookup;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.websocket.DnsQueryEventService;
import dk.mada.dns.websocket.dto.DnsQueryEventDto;
import dk.mada.dns.websocket.dto.EventTypeDto;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsRequests;
import dk.mada.dns.wire.model.DnsSection;

/**
 * DNS lookup, passing request on to upstream DNS server (pass-through).
 */
@ApplicationScoped
public class DnsLookupService implements UDPPacketHandler {
	private static final String DNS_ECHO = "dns-echo.";
	private static final String DNS_BYPASS = "dns-bypass.";

	private static final Logger logger = LoggerFactory.getLogger(DnsLookupService.class);

	@Inject private DnsQueryEventService websocketEventNotifier;
	@Inject private FilteredLookup lookup;
	@Inject private DevelopmentDebugging devDebugging;

	@Override
	public ByteBuffer process(String clientIp, ByteBuffer wireRequest) {
		Objects.requireNonNull(clientIp);
		Objects.requireNonNull(wireRequest);
		
		DnsRequest request = DnsRequests.fromWireData(wireRequest);
		wireRequest.rewind();
		logger.debug("Decoded request: {}", request);
		
		Query q = new Query(request, clientIp);

		LookupResult res;
		String queryHostname = q.getRequestName();
		if (queryHostname.startsWith(DNS_ECHO)) {
			String traceHost = queryHostname.substring(DNS_ECHO.length());
			logger.info("Enable logging for {}", traceHost);
			devDebugging.startEchoForHost(traceHost);
			res = lookup.makeBlockedReply(q, "toggle:" + queryHostname);
		} else if (queryHostname.startsWith(DNS_BYPASS)) {
			String traceHost = queryHostname.substring(DNS_BYPASS.length());
			logger.info("Enable bypass for {}", traceHost);
			devDebugging.startBypassForHost(traceHost);
			res = lookup.makeBlockedReply(q, "toggle:" + queryHostname);
		} else {
			q.setDebugBypassRequest(devDebugging.isBypassForHost(queryHostname));
			res = lookup.lookup(q);
		}
		
		ByteBuffer replyBuffer = null;
		if (res.getState() == LookupState.FAILED) {
			logger.warn("Failed lookup");
			replyBuffer = doFallbackUpstreamRequest(request);
		} else if (res.getState() == LookupState.BYPASS) {
			logger.warn("Bypass lookup");
			replyBuffer = res.getReply().getOptWireReply();
		}
		
		if (replyBuffer == null) {
			DnsReply reply = res.getReply();
			logger.debug("Decoded reply: {}", reply);
			replyBuffer = DnsReplies.toWireFormat(reply);
		}
		
		devDebugging.devOutputWireData(queryHostname, "Filtered reply", replyBuffer);
		
		devDebugging.stopActionForHost(queryHostname);

		notifyEventListeners(queryHostname, res.getState(), res.getReply());
		
		return replyBuffer;
	}
	
	private ByteBuffer doFallbackUpstreamRequest(DnsRequest request) {
		throw new UnsupportedOperationException("FIXME: need to implement safe fall-back");
	}

	private void notifyEventListeners(String queryHostName, LookupState state, DnsReply reply) {
		
		DnsSection answers = reply.getAnswer();
		if (answers == null) {
			return;
		}
		
		List<DnsRecord> records = answers.getRecords();
		if (records == null || records.isEmpty()) {
			return;
		}
		
		DnsRecord firstAnswer = records.get(0);

		DnsQueryEventDto dto = new DnsQueryEventDto();
		dto.hostname = queryHostName;
//		dto.ttl = firstAnswer.getTtl();
//		firstAnswer.asRecordA()
//			.ifPresent(dra -> dto.ip = dra.getAddress().getHostAddress());
		dto.type = EventTypeDto.from(state);

		logger.debug("Notify listeners about {}", dto);
		
		websocketEventNotifier.broadcast(dto);
	}
}
