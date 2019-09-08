package dk.mada.dns.wire.model.conversion;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import javax.enterprise.context.ApplicationScoped;

import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import dk.mada.dns.wire.model.DnsClass;
import dk.mada.dns.wire.model.DnsHeader;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;

@ApplicationScoped
public class ModelToWireXbill {
	public ByteBuffer modelToWire(DnsReply reply) throws IOException {
		String hostname = reply.getQuestion().getName().getName();
		
    	String absName = hostname.endsWith(".") ? hostname : (hostname + ".");
    	Name name = new Name(absName);
    	Record question = Record.newRecord(name, DnsRecordType.A.getWireValue(), DnsClass.IN.getWireValue());

    	Message message = Message.newQuery(question);

    	DnsHeader header = reply.getHeader();
    	message.setHeader(new Header(header.toWireFormat()));
    	
    	reply.getAnswer().stream()
    		.map(this::toRecord)
    		.forEach(r -> message.addRecord(r, Section.ANSWER));
    	
    	return ByteBuffer.wrap(message.toWire());
	}
	
	private Record toRecord(DnsRecord r) {
		try {
			Name name = new Name(r.getName().getName());
			return Record.newRecord(name, r.getRecordType().getWireValue(), r.getDnsClass().getWireValue(), r.getTtl());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
