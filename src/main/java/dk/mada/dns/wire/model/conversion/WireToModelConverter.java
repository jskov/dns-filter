package dk.mada.dns.wire.model.conversion;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.EDNSOption;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.TXTRecord;

import dk.mada.dns.util.Hexer;
import dk.mada.dns.wire.model.DnsClass;
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
import dk.mada.dns.wire.model.DnsSectionAnswer;
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
		
		return DnsRequests.fromWireRequest(toRequestHeader(header, additional.getSize()), DnsSections.ofQuestion(toModelRecord(question, true)), additional, wireBytes);
	}

	private static DnsReply _replyToModel(ByteBuffer reply) throws IOException {
		var message = new Message(reply);

		logger.debug("toModel {} {}", reply.position(), reply.limit());
		reply.flip();
		logger.debug("toModel/r {} {}", reply.position(), reply.limit());
		
		return fromAnswers(message.getHeader(), message.getQuestion(), message.getSectionArray(Section.ANSWER), message.getSectionArray(Section.ADDITIONAL), reply);
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
    	
    	DnsSectionAdditional additionalSection = toAdditionalSection(additionalRecords);
		DnsSectionAnswer answerSecion = DnsSections.ofAnswers(answers);
		return DnsReplies.fromAnswer(toReplyHeader(header, answerSecion.getSize(), additionalSection.getSize()), DnsSections.ofQuestion(toModelRecord(question, true)), answerSecion, additionalSection, optWireData);
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
	
	private static DnsHeaderReply toReplyHeader(Header h, short ancount, short arcount) {
		
		logger.debug("xbill header {} : {}", h.printFlags(), h.getRcode());
		
		short flags = DnsHeader.FLAGS_QR;

		if (h.getFlag(Flags.AA)) {
			flags |= DnsHeader.FLAGS_AA;
		}
		if (h.getFlag(Flags.TC)) {
			flags |= DnsHeader.FLAGS_TC;
		}
		if (h.getFlag(Flags.RD)) {
			flags |= DnsHeader.FLAGS_RD;
		}
		if (h.getFlag(Flags.RA)) {
			flags |= DnsHeader.FLAGS_RA;
		}
		if (h.getFlag(Flags.AD)) {
			flags |= DnsHeader.FLAGS_AD;
		}
		if (h.getFlag(Flags.CD)) {
			flags |= DnsHeader.FLAGS_CD;
		}
		flags |= h.getRcode();

		logger.debug("Model flags: {}", Hexer.hexShort(flags));
		
		short qdcount = 1;
		short nscount = 0;
		
		
		return DnsHeaderReplies.fromWire((short)h.getID(), flags, qdcount, (short)ancount, nscount, arcount);
	}
	
	private static DnsHeaderQuery toRequestHeader(Header h, short arcount) {
		byte[] bytes = h.toWire();
		
		short flags = (short)(bytes[2] << 8 | bytes[3]);
		short qdcount = 1;
		short ancount = 0;
		short nscount = 0;
		
		return DnsHeaderQueries.newObsoleted((short)h.getID(), flags, qdcount, ancount, nscount, arcount);
	}

	private static DnsRecord toModelRecord(Record r, boolean isQuestion) {
		var type = DnsRecordType.fromWireValue(r.getType());
		var name = DnsName.fromName(r.getName().toString(true));
		long ttl = r.getTTL();

		if (isQuestion) {
			return DnsRecords.qRecordFrom(name, type);
		}
		
		logger.debug("Processing xbill type {}", r.getClass());
		
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
			logger.debug("Opt record {}", optRec);
			@SuppressWarnings("unchecked")
			List<EDNSOption> xopts = (List<EDNSOption>)optRec.getOptions();
			
			short payloadSize = (short)optRec.getPayloadSize();
			short flags = (short)optRec.getFlags();
			byte xrcode = (byte)optRec.getExtendedRcode();
			byte version = (byte)optRec.getVersion();
			List<DnsOption> options = xopts.stream()
					.map(o -> toDnsOption(o))
					.collect(toList());
			
			return DnsRecords.optRecordFrom(name, type, payloadSize, xrcode, version, flags, options);
		} else if (r instanceof TXTRecord) {
			TXTRecord txtRecord = (TXTRecord)r;
			var dnsClass = DnsClass.fromWire(r.getDClass());
			@SuppressWarnings("unchecked")
			List<String> txts = (List<String>)txtRecord.getStrings();
			
			logger.debug("TXT record {} : {}", name, txts);
			
			return DnsRecords.txtRecordFrom(name, dnsClass, ttl, txts);
		}
		
		logger.warn("Unknown record type {} : {}", type, r.getClass());
		
		return DnsRecord.unknownFrom(type, name, ttl);
	}
	
	private static DnsOption toDnsOption(EDNSOption xopt) {
		logger.info("Unknown DNS option {}", xopt.getClass());
		return new DnsOptionUnhandled();
	}
}
