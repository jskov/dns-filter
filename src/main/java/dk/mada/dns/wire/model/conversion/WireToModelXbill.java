package dk.mada.dns.wire.model.conversion;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import dk.mada.dns.wire.model.DnsName;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;
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
	
	private DnsReply _replyToModel(ByteBuffer reply) throws IOException {
		var message = new Message(reply);
		return fromAnswers(message.getSectionArray(Section.ANSWER));
	}
	
	public DnsReply fromAnswers(Record[] answerRecords) {
    	List<DnsRecord> answers = Arrays.stream(answerRecords)
    		.map(this::toModelRecord)
    		.collect(Collectors.toList());
		
		var m = new DnsReply();
		m.setAnswer(DnsSection.ofAnswers(answers));
		return m;
	}
	
	private DnsRecord toModelRecord(Record r) {
		var type = DnsRecordType.fromWireValue(r.getType());
		var name = DnsName.fromName(r.getName().toString(true));
		long ttl = r.getTTL();
		
		if (r instanceof ARecord) {
			var address = ((ARecord)r).getAddress();
			return DnsRecordA.from(name, address, ttl);
		}
		
		return DnsRecord.unknownFrom(type, name, ttl);
	}
}
