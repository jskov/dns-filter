package dk.mada.dns.wire.model.conversion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import dk.mada.dns.wire.model.DnsName;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsRecordQ;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsSection;

@ApplicationScoped
public class WireToModelXbill {

	public DnsReply replyToModel(ByteBuffer reply) {
		try {
			return _replyToModel(reply);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to convert reply to model", e);
		}
	}

	public DnsRequest requestToModel(ByteBuffer request) {
		try {
			return _requestToModel(request);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to convert request to model", e);
		}
	}

	private DnsRequest _requestToModel(ByteBuffer request) throws IOException {
		var message = new Message(request);
		var question = message.getQuestion();
		return DnsRequest.fromWireRequest(DnsSection.ofQuestion(toModelRecord(question, true)), request);
	}

	private DnsReply _replyToModel(ByteBuffer reply) throws IOException {
		var message = new Message(reply);
		return fromAnswers(message.getQuestion(), message.getSectionArray(Section.ANSWER));
	}
	
	public DnsReply fromAnswers(Record _question, Record[] _answerRecords) {
		Record question = Objects.requireNonNull(_question, "Must provide question");
    	Record[] answerRecords = Objects.requireNonNull(_answerRecords, "Must provide answers");
		List<DnsRecord> answers = Arrays.stream(answerRecords)
    		.map(r -> toModelRecord(r, false))
    		.collect(Collectors.toList());
		
    	return DnsReply.fromAnswer(DnsSection.ofQuestion(toModelRecord(question, true)), DnsSection.ofAnswers(answers));
	}
	
	private DnsRecord toModelRecord(Record r, boolean isQuestion) {
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
