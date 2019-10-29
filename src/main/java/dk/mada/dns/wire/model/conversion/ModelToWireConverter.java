package dk.mada.dns.wire.model.conversion;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import dk.mada.dns.wire.model.DnsClass;
import dk.mada.dns.wire.model.DnsHeader;
import dk.mada.dns.wire.model.DnsOption;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsRecordOpt;
import dk.mada.dns.wire.model.DnsReply;

/**
 * Convert model to wire format using xbill.dns.
 */
public class ModelToWireConverter {
	private static final Logger logger = LoggerFactory.getLogger(ModelToWireConverter.class);
	public static ByteBuffer modelToWire(DnsReply reply) {
		try {
			return _modelToWire(reply);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to convert reply model to wire", e);
		}
	}

	public static ByteBuffer _modelToWire(DnsReply reply) throws IOException {
		String hostname = reply.getQuestion().getName().getName();
		
    	String absName = hostname.endsWith(".") ? hostname : (hostname + ".");
    	Name name = new Name(absName);
    	int type = reply.getQuestion().getRecordType().getWireValue();
    	Record question = Record.newRecord(name, type, DnsClass.IN.getWireValue());

    	Message message = Message.newQuery(question);

    	DnsHeader header = reply.getHeader();
    	message.setHeader(new Header(header.toWireFormatZeroAnswers()));
    	
    	reply.getAnswer().stream()
    		.map(a -> toRecord(a))
    		.forEach(r -> message.addRecord(r, Section.ANSWER));
    	
    	reply.getAdditional().stream()
    	.peek(a -> logger.info("GO FOR {}", a))
    		.map(a -> toRecord(a))
    		.forEach(r -> message.addRecord(r, Section.ADDITIONAL));
    
    	logger.debug("Converted {} to\n{}", reply, message);
    	
    	return ByteBuffer.wrap(message.toWire());
	}
	
	private static Record toRecord(DnsRecord r) {
		if (r instanceof DnsRecordA) {
			return toRecord((DnsRecordA)r);
		}
		
		if (r instanceof DnsRecordOpt) {
			return toRecord((DnsRecordOpt)r);
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

	private static Record toRecord(DnsRecordOpt r) {
		short payloadSize = r.getPayloadSize();
		byte xrcode = r.getXrcode();
		byte version = r.getVersion();
		short flags = r.getFlags();
		
		if (!r.getOptions().isEmpty()) {
			logger.warn("Cannot convert options to xbill api");
		}
		
		List<Object> xbillOptions = List.of();
		
		return new OPTRecord(payloadSize, xrcode, version, flags, xbillOptions);
	}
	
	private static Record toRecord(DnsRecordA r) {
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
