package dk.mada.dns.wire.model.conversion;

import java.nio.ByteBuffer;

import javax.enterprise.context.ApplicationScoped;

import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.wire.model.DnsClass;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;

@ApplicationScoped
public class ModelToWireXbill {
	public ByteBuffer modelToWire(DnsReply reply) throws TextParseException {
		String hostname = reply.getQuestion().getName().getName();
		
    	String absName = hostname.endsWith(".") ? hostname : (hostname + ".");
    	Name name = new Name(absName);
    	Record question = Record.newRecord(name, DnsRecordType.A.getWireValue(), DnsClass.IN.getWireValue());

    	// FIXME: need header
    	
    	return null;
	}
}
