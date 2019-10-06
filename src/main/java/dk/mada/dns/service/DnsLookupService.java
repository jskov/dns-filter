package dk.mada.dns.service;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.resolver.UpstreamResolver;
import dk.mada.dns.websocket.DnsQueryEventService;
import dk.mada.dns.websocket.dto.DnsQueryEventDto;
import dk.mada.dns.websocket.dto.EventTypeDto;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsSection;
import dk.mada.dns.wire.model.conversion.ModelToWireConverter;
import dk.mada.dns.wire.model.conversion.WireToModelConverter;

/**
 * DNS lookup, passing request on to upstream DNS server (pass-through).
 */
@ApplicationScoped
public class DnsLookupService implements UDPPacketHandler {
	private static final Logger logger = LoggerFactory.getLogger(DnsLookupService.class);

	@Inject private DnsQueryEventService websocketEventNotifier;
	@Inject private UpstreamResolver resolver;
	@Inject private WireToModelConverter wireToModelConverter;
	@Inject private ModelToWireConverter modelToWireConverter;
	@Inject private DevelopmentDebugging devDebugging;

	@Override
	public ByteBuffer process(String clientIp, ByteBuffer wireRequest) {
		Objects.requireNonNull(clientIp);
		Objects.requireNonNull(wireRequest);
		
		DnsRequest request = wireToModelConverter.requestToModel(wireRequest);
		wireRequest.rewind();
		logger.info("Decoded request: {}", request);
		devDebugging.devOutputRequest(request);
		
		Optional<DnsReply> reply = resolver.resolve(clientIp, request);
		logger.info("Decoded reply: {}", reply);

		
		ByteBuffer replyBuffer = reply
				.map(this::reportAndConvertReply)
				.orElseGet(() -> doFallbackUpstreamRequest(request));
		
		return replyBuffer;
	}
	
	private ByteBuffer reportAndConvertReply(DnsReply reply) {
		notifyEventListeners(reply);

		return modelToWireConverter.modelToWire(reply);
	}
	
	private ByteBuffer doFallbackUpstreamRequest(DnsRequest request) {
		throw new UnsupportedOperationException("FIXME: need to implement safe fall-back");
	}

	private void notifyEventListeners(DnsReply reply) {
		
		DnsSection answers = reply.getAnswer();
		if (answers == null) {
			throw new IllegalStateException("No answers");
		}
		
		List<DnsRecord> records = answers.getRecords();
		if (records == null || records.isEmpty()) {
			throw new IllegalStateException("No usable answer records");
		}
		
		DnsRecord firstAnswer = records.get(0);

		DnsQueryEventDto dto = new DnsQueryEventDto();
		dto.hostname = reply.getQuestion().getName().toString();
		dto.ttl = firstAnswer.getTtl();
		firstAnswer.asRecordA()
			.ifPresent(dra -> dto.ip = dra.getAddress().getHostAddress());
		dto.type = EventTypeDto.PASSTHROUGH;

		logger.info("Notify listeners about {}", dto);
		
		websocketEventNotifier.broadcast(dto);
	}
}
