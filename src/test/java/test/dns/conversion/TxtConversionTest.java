package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.NOSCRIPT_CSP_INVALID;
import static fixture.dns.wiredata.TestQueries.NOSCRIPT_CSP_INVALID_REPLY;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Deny;
import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.Allow;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsResponseCode;
import fixture.resolver.CannedUdpResolver;

public class TxtConversionTest {
	/**
	 * Tests handling of TXT records.
	 * Lookup of .invalid causes a nice (single-entry) TXT record reply.
	 */
	@Test
	public void handlesTxtQuery() {
		Query q = makeTestQuery(NOSCRIPT_CSP_INVALID);

		Resolver resolver = new CannedUdpResolver(NOSCRIPT_CSP_INVALID_REPLY);
		Deny deny = h -> false;
		Allow allow = h -> false;
		Block block = h -> false;
		
		var sut = new LookupEngine(resolver, block, deny, allow);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.PASSTHROUGH);

		DnsReply reply = result.getReply();
		DnsResponseCode responseCode = reply.getHeader().getResponseCode();
		
		assertThat(responseCode)
			.isEqualTo(DnsResponseCode.NXDOMAIN);
		List<String> txtRecord = reply.getAdditional().getRecords().stream()
				.flatMap(r -> r.asRecordTxt().stream())
				.flatMap(t -> t.getTxts().stream())
				.collect(toList());
		
		assertThat(txtRecord)
			.isNotEmpty()
			.anyMatch(s -> s.contains("Blocking is mandated by standards"));
	}
}
