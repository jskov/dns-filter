package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.STORE_EASYBRAIN_REPLY;
import static fixture.dns.wiredata.TestQueries.STORE_EASYBRAIN_REQUEST;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Allow;
import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.Deny;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import fixture.resolver.CannedUdpResolver;

public class HttpsConversionTest {
	public static final Logger logger = LoggerFactory.getLogger(HttpsConversionTest.class);

	/**
	 * Tests non-explosion handling of HTTPS records.
	 * FIXME: not sure what to make of HTTPS records.
	 */
	@Test
	public void handlesHttpsQuery() {
		Query q = makeTestQuery(STORE_EASYBRAIN_REQUEST);
		
		Resolver resolver = new CannedUdpResolver(STORE_EASYBRAIN_REPLY);
		//Resolver resolver = TestQueries.makeExternalLoggingResolver(); 
		Deny deny = h -> false;
		Allow allow = h -> false;
		Block block = h -> false;
		
		var sut = new LookupEngine(resolver, allow, deny, block);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.PASSTHROUGH);
	}
}
