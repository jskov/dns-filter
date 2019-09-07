package fixture.dns;

import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.Application;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.conversion.WireToModelXbill;

@ApplicationScoped
public class DnsPayloadHelper {
	@Inject private WireToModelXbill wireToModel;
	
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
    	
    	return wireToModel.fromAnswers(res);
	}
	
	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
    	localhostResolver.setPort(Application.DNS_LISTENING_PORT);
    	return localhostResolver;
	}
}
