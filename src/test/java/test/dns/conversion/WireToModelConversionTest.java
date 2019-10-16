package test.dns.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.resolver.UpstreamResolver;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsRequests;
import fixture.dns.wiredata.TestQueries;

public class WireToModelConversionTest {

	
	/**
	 * DNS requests of type AAAA (IPv6) were not handled out of the box.
	 */
	@Test
	public void conversionShouldHandleTrpe28() {
		DnsRequest request = DnsRequests.fromWireData(TestQueries.MOZILLA_ORG_AAAA);

		Optional<DnsReply> reply = new UpstreamResolver().resolve("127.0.0.1", request);
		
		assertThat(request)
			.isNotNull();
		
		assertThat(reply)
			.isEmpty();
	}
}
