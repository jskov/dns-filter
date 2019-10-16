package inttest.dns.filter;

import static fixture.dns.wiredata.TestQueries.ADNXS_COM;
import static fixture.dns.wiredata.TestQueries.getAdnxsChainedReply;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Blockedlist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.filter.blocker.FetchLists;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.wire.model.DnsReply;
import fixture.resolver.TestResolver;

/**
 * Tests that external filters can be loaded and provide some filtering.
 */
@Tag("integration")
public class FilterImportIntTest {
	@Test
	public void hostnamesShouldMatchDirectly() throws UnknownHostException {
		Blockedlist blockedlist = new FetchLists().fetch();

		Query q = makeTestQuery(ADNXS_COM);
		DnsReply reply = getAdnxsChainedReply(q);

		TestResolver resolver = new TestResolver(reply);
		Blacklist blacklist = h -> false;
		Whitelist whitelist = h -> false;
		
		var sut = new LookupEngine(resolver, blockedlist, blacklist, whitelist);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.BLOCKED);
	}
}
