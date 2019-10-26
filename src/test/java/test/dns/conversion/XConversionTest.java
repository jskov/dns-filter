package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.IMGS_XKCD_COM;
import static fixture.dns.wiredata.TestQueries.IMGS_XKCD_COM_REPLY;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Blockedlist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import fixture.resolver.CannedUdpResolver;

public class XConversionTest {
	/**
	 */
	@Test
	public void unknownProblem() {
		Query q = makeTestQuery(IMGS_XKCD_COM);

		Resolver resolver = new CannedUdpResolver(IMGS_XKCD_COM_REPLY);
		Blacklist blacklist = h -> false;
		Whitelist whitelist = h -> false;
		Blockedlist blockedlist = h -> false;
		
		var sut = new LookupEngine(resolver, blockedlist, blacklist, whitelist);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.BLACKLISTED);
	}
}
