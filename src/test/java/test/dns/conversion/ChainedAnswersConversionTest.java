package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.IMGS_XKCD_COM;
import static fixture.dns.wiredata.TestQueries.IMGS_XKCD_COM_REPLY;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Deny;
import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.Allow;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.conversion.ModelToWireConverter;
import fixture.resolver.CannedUdpResolver;

/**
 * Tests conversion of chained answers.
 */
public class ChainedAnswersConversionTest {
	/**
	 * Test CNAME->A chain.
	 * Also has an (empty) OPT section.
	 */
	@Test
	public void cnameToAConversionWorks() {
		Query q = makeTestQuery(IMGS_XKCD_COM);

		Resolver resolver = new CannedUdpResolver(IMGS_XKCD_COM_REPLY);
		Deny deny = h -> false;
		Allow allow = h -> false;
		Block block = h -> false;
		
		var sut = new LookupEngine(resolver, allow, deny, block);
		LookupResult result = sut.lookup(q);
		
		q.setLookupResult(result);
		
		ByteBuffer bb = ModelToWireConverter.modelToWire(q);

		// Not really a good test - but parsed correctly by dig
		assertThat(bb.limit())
			.isEqualTo(105);
	}
}
