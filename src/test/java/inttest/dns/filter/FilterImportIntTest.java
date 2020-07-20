package inttest.dns.filter;

import static fixture.dns.wiredata.TestQueries.ADNXS_COM;
import static fixture.dns.wiredata.TestQueries.ADNXS_COM_REPLY;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dk.mada.dns.Environment;
import dk.mada.dns.filter.Deny;
import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.Allow;
import dk.mada.dns.filter.blocker.BlockedListCacher;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import fixture.resolver.CannedUdpResolver;

/**
 * Tests that external filters can be loaded and provide some filtering.
 */
@Tag("integration")
public class FilterImportIntTest {
	@Test
	public void blockingWorks() throws UnknownHostException {
		BlockedListCacher cacher = new BlockedListCacher(new Environment());
		cacher.preloadCache();
		Block blockedlist = cacher.get();

		Query q = makeTestQuery(ADNXS_COM);

		Resolver resolver = new CannedUdpResolver(ADNXS_COM_REPLY);
		Deny deny = h -> false;
		Allow allow = h -> false;
		
		var sut = new LookupEngine(resolver, allow, deny, blockedlist);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.BLOCKED);
	}
}
