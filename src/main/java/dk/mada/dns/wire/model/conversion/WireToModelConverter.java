package dk.mada.dns.wire.model.conversion;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.EDNSOption;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import dk.mada.dns.wire.model.DnsHeader;
import dk.mada.dns.wire.model.DnsHeaderQueries;
import dk.mada.dns.wire.model.DnsHeaderQuery;
import dk.mada.dns.wire.model.DnsHeaderReplies;
import dk.mada.dns.wire.model.DnsHeaderReply;
import dk.mada.dns.wire.model.DnsName;
import dk.mada.dns.wire.model.DnsOption;
import dk.mada.dns.wire.model.DnsOptionUnhandled;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsRecords;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsRequests;
import dk.mada.dns.wire.model.DnsSectionAdditional;
import dk.mada.dns.wire.model.DnsSections;

/**
 * Convert wire format to model using xbill.dns.
 */
public class WireToModelConverter {
	private static final Logger logger = LoggerFactory.getLogger(WireToModelConverter.class);
	
	public static DnsReply replyToModel(ByteBuffer reply) {
		try {
			return _replyToModel(reply);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to convert reply to model", e);
		}
	}

	public static DnsRequest requestToModel(ByteBuffer request) {
		try {
			return _requestToModel(request);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to convert request to model", e);
		}
	}

	private static DnsRequest _requestToModel(ByteBuffer request) throws IOException {
		var wireBytes = request.duplicate();

		var message = new Message(request);
		var question = message.getQuestion();
		var header = message.getHeader();

		DnsSectionAdditional additional = toAdditionalSection(message.getSectionArray(Section.ADDITIONAL));
		
		return DnsRequests.fromWireRequest(toRequestHeader(header, 0), DnsSections.ofQuestion(toModelRecord(question, true)), additional, wireBytes);
	}

	private static DnsReply _replyToModel(ByteBuffer reply) throws IOException {
		var message = new Message(reply);

		logger.debug("toModel {} {}", reply.position(), reply.limit());
		reply.flip();
		logger.debug("toModel/r {} {}", reply.position(), reply.limit());
		
		logger.info("Processing message {}", message);
		message.getOPT();
		
		return fromAnswers(message.getHeader(), message.getQuestion(), message.getSectionArray(Section.ANSWER), null, reply);
	}
	
	public static DnsReply fromAnswers(Header _header, Record _question, Record[] answerRecords) {
		return fromAnswers(_header, _question, answerRecords, null, null);
	}

	public static DnsReply fromAnswers(Header _header, Record _question, Record[] answerRecords, Record[] additionalRecords, ByteBuffer optWireData) {
		Header header = Objects.requireNonNull(_header, "Must provide header");
		Record question = Objects.requireNonNull(_question, "Must provide question");

    	List<DnsRecord> answers;
    	if (answerRecords == null) {
    		answers = List.of();
    	} else {
    		answers = Arrays.stream(answerRecords)
			    		.map(r -> toModelRecord(r, false))
			    		.collect(toList());
    	}
    	
    	return DnsReplies.fromAnswer(toReplyHeader(header, answers.size()), DnsSections.ofQuestion(toModelRecord(question, true)), DnsSections.ofAnswers(answers), toAdditionalSection(additionalRecords), optWireData);
	}

	private static DnsSectionAdditional toAdditionalSection(Record[] additionalRecords) {
		List<DnsRecord> additional;
		if (additionalRecords == null) {
			additional = List.of();
		} else {
			additional = Arrays.stream(additionalRecords)
					.map(r -> toModelRecord(r, false))
					.collect(toList());
		}
		return DnsSections.ofAdditionals(additional);
	}
	
	private static DnsHeaderReply toReplyHeader(Header h, int ancount) {
		short flags = DnsHeader.FLAGS_QR;
		short qdcount = 1;
		short nscount = 0;
		short arcount = 0;
		
		return DnsHeaderReplies.newObsoleted((short)h.getID(), flags, qdcount, (short)ancount, nscount, arcount);
	}

	private static DnsHeaderQuery toRequestHeader(Header h, int ancount) {
		byte[] bytes = h.toWire();
		
		short flags = (short)(bytes[2] << 8 | bytes[3]);
		short qdcount = 1;
		short nscount = 0;
		short arcount = 0;
		
		return DnsHeaderQueries.newObsoleted((short)h.getID(), flags, qdcount, (short)ancount, nscount, arcount);
	}

	private static DnsRecord toModelRecord(Record r, boolean isQuestion) {
		var type = DnsRecordType.fromWireValue(r.getType());
		var name = DnsName.fromName(r.getName().toString(true));
		long ttl = r.getTTL();

		if (isQuestion) {
			return DnsRecords.qRecordFrom(name, type);
		}
		
		logger.info("Processing xbill type {}", r.getClass());
		
		if (r instanceof ARecord) {
			var address = ((ARecord)r).getAddress();
			return DnsRecords.aRecordFrom(name, address, ttl);
		} else if (r instanceof AAAARecord) {
			var address = ((AAAARecord)r).getAddress();
			return DnsRecords.aaaaRecordFrom(name, address, ttl);
		} else if (r instanceof CNAMERecord) {
			var alias = ((CNAMERecord)r).getAlias();
			logger.debug("CRecord {} -> {}", name, alias);
			return DnsRecords.cRecordFrom(name, DnsName.fromName(alias.toString(true)), ttl);
		} else if (r instanceof OPTRecord) {
			var optRec = (OPTRecord)r;
			logger.info("Opt record {}", optRec);
			@SuppressWarnings("unchecked")
			List<EDNSOption> xopts = (List<EDNSOption>)optRec.getOptions();
			
			int payloadSize = optRec.getPayloadSize();
			int flags = optRec.getFlags();
			List<DnsOption> options = xopts.stream()
					.map(o -> toDnsOption(o))
					.collect(toList());
			
			return DnsRecords.optRecordFrom(name, type, payloadSize, flags, options);
		}
		
		return DnsRecord.unknownFrom(type, name, ttl);
	}
	
	private static DnsOption toDnsOption(EDNSOption xopt) {
		logger.info("Unknown DNS option {}", xopt.getClass());
		return new DnsOptionUnhandled();
	}
}
