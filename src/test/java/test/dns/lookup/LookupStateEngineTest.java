package test.dns.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.wire.model.DnsRequests;
import fixture.dns.wiredata.TestQueries;
import fixture.resolver.TestResolver;

/**
 * Tests state engine behavior.
 */
public class LookupStateEngineTest {
	/**
	 * A query for a blacklisted entry should not cause
	 * upstream resolve.
	 */
	@Test
	public void blacklistedEntriesShouldNotBeResolved() {
		Query q = makeTestQuery(TestQueries.GOOGLEADSERVICES_COM);
		
		TestResolver resolver = new TestResolver();
		Blacklist blacklist = h -> h.contains("ads");
		Whitelist whitelist = h -> false;
		
		var sut = new LookupEngine(resolver, blacklist, whitelist);
		LookupResult result = sut.lookup(q);
		
		assertThat(result.getState())
			.isEqualTo(LookupState.BLACKLISTED);
		assertThat(resolver.hasBeenCalled())
			.isFalse();
	}
	
	/*
	 * A query for an innocent name may resolve to a chain of C-names
	 * before ending in an A-record. Any element in the chain may be
	 * blacklisted.
	 */
	@Test
	public void blacklistedChainEntriesShouldBlock() {
		// ;; ANSWERS:                                                                                                                                                                                                        
//		detectportal.firefox.com.       11      IN      CNAME                                                    
//		detectportal.prod.mozaws.net.   60      IN      CNAME                                                    
//		detectportal.firefox.com-v2.edgesuite.net.      9590    IN      CNAME                                    
//		a1089.dscd.akamai.net.  10      IN      A       95.101.142.120                                           
//		a1089.dscd.akamai.net.  10      IN      A       104.84.152.177


		Query q = makeTestQuery(TestQueries.DETECTPORTAL_FIREFOX_COM);
		
		TestResolver resolver = new TestResolver();
		Blacklist blacklist = h -> h.contains("ads");
		Whitelist whitelist = h -> false;

		var sut = new LookupEngine(resolver, blacklist, whitelist);
		LookupResult result = sut.lookup(q);

		System.out.println(result);

		assertThat(result.getState())
			.isEqualTo(LookupState.BLACKLISTED);
}

	/**
	 * A whitelisted query (or element in c-name chain) should
	 * return resolved IP, even if the query/chain also contains
	 * blacklisted entries.
	 * Note that a cache/upstream resolve is always needed (the
	 * IP is needed, after all).
	 */
	@Test
	public void whitelistedEntriesShouldBeResolved() {
		// TODO: write test
	}
	
	/**
	 * All lookups (including whitelisted and blacklisted) should
	 * be cached, observing TTL.
	 * The cache should be preferred to upstream lookup.
	 */
	@Test
	public void resolvedRepliesShouldBeCached() {
		// TODO: write test
	}
	
	
	
	private Query makeTestQuery(byte[] data) {
		var req = DnsRequests.fromWireData(data);
		
		var query = new Query(req, "127.0.0.1");
		return query;
	}
	
	
	
}
