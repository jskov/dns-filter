package fixture.dns;

import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.Application;
import dk.mada.dns.wire.model.DnsClass;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.conversion.WireToModelConverter;

@ApplicationScoped
public class DnsPayloadHelper {
	@Inject private WireToModelConverter wireToModel;
	
	public DnsReply serviceDnsLookup(String hostname) {
		try {
			return xbillDnsLookup(hostname);
		} catch (TextParseException | UnknownHostException e) {
			throw new IllegalStateException("Failed to make DNS lookup for " + hostname, e);
		}
	}
	
	private DnsReply xbillDnsLookup(String hostname) throws TextParseException, UnknownHostException {
    	Lookup lookup = new Lookup(hostname);
    	lookup.setResolver(getLocalhostResolver());
    	lookup.setCache(null);
    	lookup.setSearchPath(new String[] {});
  
    	Record[] res = lookup.run();

    	String absName = hostname.endsWith(".") ? hostname : (hostname + ".");
    	Name name = new Name(absName);
    	Record question = Record.newRecord(name, DnsRecordType.A.getWireValue(), DnsClass.IN.getWireValue());
    	
    	Message message = Message.newQuery(question);
    	
    	return wireToModel.fromAnswers(message.getHeader(), question, res);
	}
	
	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
    	localhostResolver.setPort(Application.DNS_LISTENING_PORT);
    	return localhostResolver;
	}
}
