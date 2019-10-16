package test.dns.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import dk.mada.dns.resolver.UpstreamResolver;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsRequests;
import fixture.dns.wiredata.TestQueries;

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
		DnsRequest request = DnsRequests.fromWireData(TestQueries.MOZILLA_ORG_AAAA);

		Optional<DnsReply> reply = new UpstreamResolver().resolve("127.0.0.1", request);

		assertThat(reply)
			.get()
			.satisfies(r -> r.getAnswer().getRecords().isEmpty())
			.extracting(DnsReply::getAuthority)
				.isNull();
	}
}
