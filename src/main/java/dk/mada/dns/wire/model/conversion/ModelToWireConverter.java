package dk.mada.dns.wire.model.conversion;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.wire.model.DnsClass;
import dk.mada.dns.wire.model.DnsHeader;
import dk.mada.dns.wire.model.DnsName;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsRecordAAAA;
import dk.mada.dns.wire.model.DnsRecordC;
import dk.mada.dns.wire.model.DnsRecordOpt;
import dk.mada.dns.wire.model.DnsRecordTxt;
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
    	message.setHeader(new Header(header.toWireFormatZeroForReply()));
    	
    	reply.getAnswer().stream()
    		.map(a -> toRecord(a))
    		.forEach(r -> message.addRecord(r, Section.ANSWER));
    	
    	reply.getAdditional().stream()
    		.map(a -> toRecord(a))
    		.forEach(r -> message.addRecord(r, Section.ADDITIONAL));
    
    	logger.debug("Converted {} to\n{}", reply, message);
    	
    	return ByteBuffer.wrap(message.toWire());
	}
	
	private static Record toRecord(DnsRecord r) {
		if (r instanceof DnsRecordA) {
			return toRecord((DnsRecordA)r);
		}

		if (r instanceof DnsRecordAAAA) {
			return toRecord((DnsRecordAAAA)r);
		}

		if (r instanceof DnsRecordOpt) {
			return toRecord((DnsRecordOpt)r);
		}
		
		if (r instanceof DnsRecordC) {
			return toRecord((DnsRecordC)r);
		}
		
		if (r instanceof DnsRecordTxt) {
			return toRecord((DnsRecordTxt)r);
		}

		throw new IllegalStateException("Unhandled type " + r.getClass());
	}

	private static Record toRecord(DnsRecordTxt r) {
		try {
			Name name = toAbsName(r.getName());
			int dnsClass = r.getDnsClass().getWireValue();
			return new TXTRecord(name, dnsClass, r.getTtl(), r.getTxts());
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

	private static Record toRecord(DnsRecordC r) {
		try {
			Name name = toAbsName(r.getName());
			Name alias = toAbsName(r.getAlias());
			int dnsClass = r.getDnsClass().getWireValue();
			return new CNAMERecord(name, dnsClass, r.getTtl(), alias);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static Record toRecord(DnsRecordA r) {
		try {
			Name name = toAbsName(r.getName());
			return new ARecord(name, r.getDnsClass().getWireValue(), r.getTtl(), r.getAddress());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Record toRecord(DnsRecordAAAA r) {
		try {
			Name name = toAbsName(r.getName());
			return new AAAARecord(name, r.getDnsClass().getWireValue(), r.getTtl(), r.getAddress());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Name toAbsName(DnsName name) throws TextParseException {
		String n = name.getName();
		String absName = n.endsWith(".") ? n : (n + ".");
		return new Name(absName);
	}
}
