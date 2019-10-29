package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.IMGS_XKCD_COM;
import static fixture.dns.wiredata.TestQueries.IMGS_XKCD_COM_REPLY;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Blockedlist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.util.Hexer;
import dk.mada.dns.wire.model.conversion.ModelToWireConverter;
import fixture.resolver.CannedUdpResolver;

public class XConversionTest {
	private static final Logger logger = LoggerFactory.getLogger(XConversionTest.class);
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
		
		logger.info("Question: {}", q.getRequest());
		
		ByteBuffer bb = ModelToWireConverter.modelToWire(result.getReply());
		
		Hexer.printForDevelopment("Reply", bb, Set.of());
		
		logger.info("res {}", result.getReply());
		

//		assertThat(result.getState())
//			.isEqualTo(LookupState.BLACKLISTED);
	}
}
