package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.MOZILLA_ORG_AAAA;
import static fixture.dns.wiredata.TestQueries.MOZILLA_ORG_AAAA_SOA_REPLY;
import static fixture.dns.wiredata.TestQueries.getMozillaOrgEmptyReply;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Blockedlist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.DefaultResolver;
import dk.mada.dns.resolver.external.ExternalDnsGateway;
import dk.mada.dns.service.DevelopmentDebugging;
import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsRequests;
import fixture.resolver.CannedModelResolver;

public class WireToModelConversionTest {

	
	/**
	 * DNS requests of type AAAA (IPv6) for mozilla.org do not return an IP
	 * address, but a SOA response.
	 * Maybe used by browser to determine that a site does not support
	 * IPv6?
	 * 
	 * For now, the SOA/authority reply is *not* returned.
	 */
	@Test
	public void currentlyIgnoresSoaResponse() {
		DnsRequest request = DnsRequests.fromWireData(MOZILLA_ORG_AAAA);

		ExternalDnsGateway dnsGateway = new ExternalDnsGateway(new DevelopmentDebugging());
		Optional<DnsReply> reply = new DefaultResolver(dnsGateway).resolve("127.0.0.1", request);

		assertThat(reply)
			.get()
			.satisfies(r -> r.getAnswer().getRecords().isEmpty())
			.extracting(DnsReply::getAuthority)
				.isNull();
	}
	
	@Test
	public void canHandleRepliesWithEmptyAnswerSection() throws UnknownHostException {
		Query q = makeTestQuery(MOZILLA_ORG_AAAA);
		DnsReply reply = getMozillaOrgEmptyReply(q);

		CannedModelResolver resolver = new CannedModelResolver(reply);
		Blacklist blacklist = h -> false;
		Whitelist whitelist = h -> false;
		Blockedlist blockedlist = h -> false;
		
		var sut = new LookupEngine(resolver, blockedlist, blacklist, whitelist);
		LookupResult result = sut.lookup(q);

		DnsReply r = DnsReplies.fromWireData(MOZILLA_ORG_AAAA_SOA_REPLY);
		
	}
	
}
