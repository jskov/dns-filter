package dk.mada.dns.wire.model.conversion;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import dk.mada.dns.wire.model.DnsClass;
import dk.mada.dns.wire.model.DnsHeader;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;

/**
 * Convert model to wire format using xbill.dns.
 */
@ApplicationScoped
public class ModelToWireConverter {
	private static final Logger logger = LoggerFactory.getLogger(ModelToWireConverter.class);
	public ByteBuffer modelToWire(DnsReply reply) {
		try {
			return _modelToWire(reply);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to convert reply model to wire", e);
		}
	}

	public ByteBuffer _modelToWire(DnsReply reply) throws IOException {
		String hostname = reply.getQuestion().getName().getName();
		
    	String absName = hostname.endsWith(".") ? hostname : (hostname + ".");
    	Name name = new Name(absName);
    	Record question = Record.newRecord(name, DnsRecordType.A.getWireValue(), DnsClass.IN.getWireValue());

    	Message message = Message.newQuery(question);

    	DnsHeader header = reply.getHeader();
    	message.setHeader(new Header(header.toWireFormatZeroAnswers()));
    	
    	reply.getAnswer().stream()
    		.map(this::toRecord)
    		.forEach(r -> message.addRecord(r, Section.ANSWER));
    
    	logger.info("Converted {} to\n{}", reply, message);
    	
    	return ByteBuffer.wrap(message.toWire());
	}
	
	private Record toRecord(DnsRecord r) {
		if (r instanceof DnsRecordA) {
			return toRecord((DnsRecordA)r);
		}
		
		try {
			String n = r.getName().getName();
			String absName = n.endsWith(".") ? n : (n + ".");
			Name name = new Name(absName);
			return Record.newRecord(name, r.getRecordType().getWireValue(), r.getDnsClass().getWireValue(), r.getTtl());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Record toRecord(DnsRecordA r) {
		try {
			String n = r.getName().getName();
			String absName = n.endsWith(".") ? n : (n + ".");
			Name name = new Name(absName);
			return new ARecord(name, r.getDnsClass().getWireValue(), r.getTtl(), r.getAddress());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}