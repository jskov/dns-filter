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
		
		String qName = q.getRequestName();
		if (qName.startsWith(DNS_ECHO)) {
			String traceHost = qName.substring(DNS_ECHO.length());
			logger.info("Enable logging for {}", traceHost);
			devDebugging.startOutputForHost(traceHost);
			res = lookup.makeBlockedReply(q, "toggle:" + qName);
		} else {
			q.setDebugEchoRequest(devDebugging.isEchoOutputForHost(qName));
			
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

			replyBuffer = reportAndConvertReply(reply);
		}
		return replyBuffer;
	}
	
	private ByteBuffer reportAndConvertReply(DnsReply reply) {
		notifyEventListeners(reply);

		return DnsReplies.toWireFormat(reply);
	}
	
	private ByteBuffer doFallbackUpstreamRequest(DnsRequest request) {
		throw new UnsupportedOperationException("FIXME: need to implement safe fall-back");
	}

	private void notifyEventListeners(DnsReply reply) {
		
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
		dto.hostname = reply.getQuestion().getName().toString();
		dto.ttl = firstAnswer.getTtl();
		firstAnswer.asRecordA()
			.ifPresent(dra -> dto.ip = dra.getAddress().getHostAddress());
		dto.type = EventTypeDto.PASSTHROUGH;

		logger.debug("Notify listeners about {}", dto);
		
		websocketEventNotifier.broadcast(dto);
	}
}
