package fixture.dns.xbill;

import java.net.UnknownHostException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.Environment;
import dk.mada.dns.wire.model.DnsClass;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.conversion.WireToModelConverter;

/**
 * DNS lookup via DNS Filter.
 * 
 * Facilitates end-to-end test.
 */
@ApplicationScoped
public class DnfFilterLocalHostLookup {
	/**
	 * Makes a dns hostname request to the service running on localhost.
	 * 
	 * @param hostname name to lookup
	 * @return reply
	 */
	public DnsReply serviceDnsLookup(String hostname) {
		try {
			return xbillDnsLookup(hostname, DnsRecordType.A);
		} catch (TextParseException | UnknownHostException e) {
			throw new IllegalStateException("Failed to make DNS A lookup for " + hostname, e);
		}
	}

	public DnsReply serviceHttpsLookup(String hostname) {
		try {
			return xbillDnsLookup(hostname, DnsRecordType.HTTPS);
		} catch (TextParseException | UnknownHostException e) {
			throw new IllegalStateException("Failed to make DNS A lookup for " + hostname, e);
		}
	}

	public DnsReply serviceDnsLookupIpv6(String hostname) {
		try {
			return xbillDnsLookup(hostname, DnsRecordType.AAAA);
		} catch (TextParseException | UnknownHostException e) {
			throw new IllegalStateException("Failed to make DNS AAAA lookup for " + hostname, e);
		}
	}

	private DnsReply xbillDnsLookup(String hostname, DnsRecordType type) throws TextParseException, UnknownHostException {
		int reqType = 65; // type.getWireValue();
    	Lookup lookup = new Lookup(hostname, reqType);
    	lookup.setResolver(getLocalhostResolver());
    	lookup.setCache(null);
//    	Lookup.setPacketLogger((a, b, c, d) -> Hexer.printForDevelopment("xbill", ByteBuffer.wrap(d), Collections.emptySet()));
    	lookup.setSearchPath(new String[] {});
  
    	Record[] resArray = lookup.run();
    	List<Record> res = resArray == null ? List.of() : List.of(resArray); 

    	String absName = hostname.endsWith(".") ? hostname : (hostname + ".");
    	Name name = new Name(absName);
    	Record question = Record.newRecord(name, reqType, DnsClass.IN.getWireValue());
    	
    	Message message = Message.newQuery(question);
    	
    	return WireToModelConverter.fromAnswers(message.getHeader(), question, res);
	}
	
	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
    	localhostResolver.setPort(Environment.LISTEN_PORT_DNS_DEFAULT);
    	return localhostResolver;
	}
}
