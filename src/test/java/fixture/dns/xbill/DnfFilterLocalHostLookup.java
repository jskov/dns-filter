package fixture.dns.xbill;

import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;

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
	
	public DnsReply serviceDnsLookupIpv6(String hostname) {
		try {
			return xbillDnsLookup(hostname, DnsRecordType.AAAA);
		} catch (TextParseException | UnknownHostException e) {
			throw new IllegalStateException("Failed to make DNS AAAA lookup for " + hostname, e);
		}
	}

	private DnsReply xbillDnsLookup(String hostname, DnsRecordType type) throws TextParseException, UnknownHostException {
    	Lookup lookup = new Lookup(hostname, type.getWireValue());
    	lookup.setResolver(getLocalhostResolver());
    	lookup.setCache(null);
//    	Lookup.setPacketLogger((a, b, c, d) -> Hexer.printForDevelopment("xbill", ByteBuffer.wrap(d), Collections.emptySet()));
    	lookup.setSearchPath(new String[] {});
  
    	Record[] res = lookup.run();

    	String absName = hostname.endsWith(".") ? hostname : (hostname + ".");
    	Name name = new Name(absName);
    	Record question = Record.newRecord(name, type.getWireValue(), DnsClass.IN.getWireValue());
    	
    	Message message = Message.newQuery(question);
    	
    	return WireToModelConverter.fromAnswers(message.getHeader(), question, res);
	}
	
	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
    	localhostResolver.setPort(Application.DNS_LISTENING_PORT);
    	return localhostResolver;
	}
}
