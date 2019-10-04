package test.dns.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.service.UDPPacketHandler;
import dk.mada.dns.service.UDPServer;
import fixture.datagram.DatagramHelper;
import fixture.resolver.TestResolver;

/**
 * Tests state engine behavior.
 */
public class LookupStateEngineTest {
	/**
	 */
	@Test
	public void blacklistedEntriesShouldNotBeResolved() {
		
		Query q = makeTestQuery("ads.com");
		
		TestResolver resolver = new TestResolver();
		Blacklist blacklist = h -> h.contains("ads");
		Whitelist whitelist = h -> false;
		
		var sut = new LookupEngine(resolver, blacklist, whitelist);
		LookupResult result = sut.lookup(q);
		
		assertThat(result.getState())
			.isEqualTo(LookupState.WHITELISTED);
		assertThat(resolver.hasBeenCalled())
			.isTrue();
	}
	
	
	private Query makeTestQuery(String lookupHostname) {
		var query = new Query("127.0.0.1", lookupHostname);
		return query;
	}
	
}
