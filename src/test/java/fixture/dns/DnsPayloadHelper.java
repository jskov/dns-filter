package fixture.dns;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.Application;
import dk.mada.dns.wire.model.DnsName;
import dk.mada.dns.wire.model.DnsRecord;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsRecordType;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsSection;

@ApplicationScoped
public class DnsPayloadHelper {
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
    	List<DnsRecord> answers = Arrays.stream(res)
    		.map(this::toModelRecord)
    		.collect(Collectors.toList());
    	
    	DnsSection answer = DnsSection.ofAnswers(answers);
		return DnsReply.fromAnswer(answer);
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
	
	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
    	localhostResolver.setPort(Application.DNS_LISTENING_PORT);
    	return localhostResolver;
	}

}
