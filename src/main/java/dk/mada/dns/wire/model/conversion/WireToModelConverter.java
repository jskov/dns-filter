package dk.mada.dns.wire.model.conversion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import dk.mada.dns.wire.model.DnsHeader;
import dk.mada.dns.wire.model.DnsHeaderQueries;
import dk.mada.dns.wire.model.DnsHeaderQuery;
import dk.mada.dns.wire.model.DnsHeaderReplies;
import dk.mada.dns.wire.model.DnsHeaderReply;
import dk.mada.dns.wire.model.DnsName;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsRecordQ;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsRequests;
import dk.mada.dns.wire.model.DnsSections;

/**
 * Convert wire format to model using xbill.dns.
 */
public class WireToModelConverter {
	
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
		wireBytes.rewind();
		
		var message = new Message(request);
		var question = message.getQuestion();
		var header = message.getHeader();

		return DnsRequests.fromWireRequest(toRequestHeader(header, 0), DnsSections.ofQuestion(toModelRecord(question, true)), wireBytes);
	}

	private static DnsReply _replyToModel(ByteBuffer reply) throws IOException {
		var message = new Message(reply);
		
		return fromAnswers(message.getHeader(), message.getQuestion(), message.getSectionArray(Section.ANSWER));
	}
	
	public static DnsReply fromAnswers(Header _header, Record _question, Record[] _answerRecords) {
		Header header = Objects.requireNonNull(_header, "Must provide header");
		Record question = Objects.requireNonNull(_question, "Must provide question");
    	Record[] answerRecords = Objects.requireNonNull(_answerRecords, "Must provide answers");
		List<DnsRecord> answers = Arrays.stream(answerRecords)
    		.map(r -> toModelRecord(r, false))
    		.collect(Collectors.toList());
		
    	return DnsReplies.fromAnswer(toReplyHeader(header, answers.size()), DnsSections.ofQuestion(toModelRecord(question, true)), DnsSections.ofAnswers(answers));
	}

	private static DnsHeaderQuery toQueryHeader(Header h, int ancount) {
		byte[] wire = h.toWire();
		
		short flags = (short)(wire[2] << 8 | wire[3]);
		short qdcount = 1;
		short nscount = 0;
		short arcount = 0;
		
		return DnsHeaderQueries.newObsoleted((short)h.getID(), flags, qdcount, (short)ancount, nscount, arcount);
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
			return DnsRecordQ.from(name);
		}
		
		if (r instanceof ARecord) {
			var address = ((ARecord)r).getAddress();
			return DnsRecordA.from(name, address, ttl);
		}
		
		return DnsRecord.unknownFrom(type, name, ttl);
	}
}
