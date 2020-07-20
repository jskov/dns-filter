package test.dns.lookup;

import static fixture.dns.wiredata.TestQueries.DETECTPORTAL_FIREFOX_COM;
import static fixture.dns.wiredata.TestQueries.GOOGLEADSERVICES_COM;
import static fixture.dns.wiredata.TestQueries.getDetectportalFirefoxChainedReply;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Deny;
import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.Allow;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.LookupState;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.wire.model.DnsReply;
import fixture.resolver.CannedModelResolver;

/**
 * Tests state engine behavior.
 */
public class LookupStateEngineTest {
	/**
	 * A query for a denied entry should not cause
	 * upstream resolve.
	 */
	@Test
	public void deniedEntriesShouldNotBeResolved() {
		Query q = makeTestQuery(GOOGLEADSERVICES_COM);
		
		CannedModelResolver resolver = new CannedModelResolver();
		Deny deny = h -> h.contains("ads");
		Allow allow = h -> false;
		Block block = h -> false;

		var sut = new LookupEngine(resolver, block, deny, allow);
		LookupResult result = sut.lookup(q);
		
		assertThat(result.getState())
			.isEqualTo(LookupState.DENIED);
		assertThat(resolver.hasBeenCalled())
			.isFalse();
	}
	
	/*
	 * A query for an innocent name may resolve to a chain of C-names
	 * before ending in an A-record. Any element in the chain may be
	 * denied.
	 * 
	 * Use example of detectportal.firefox.com which replies
	 * 	 detectportal.firefox.com.       11      IN      CNAME                                                    
	 *   detectportal.prod.mozaws.net.   60      IN      CNAME                                                    
	 *   detectportal.firefox.com-v2.edgesuite.net 9999 IN CNAME
     *   a1089.dscd.akamai.net.  10      IN      A       95.101.142.120                                           
     *   a1089.dscd.akamai.net.  10      IN      A       104.84.152.177
	 */
	@Test
	public void deniedChainEntriesShouldBlock() throws UnknownHostException {
		Query q = makeTestQuery(DETECTPORTAL_FIREFOX_COM);
		DnsReply reply = getDetectportalFirefoxChainedReply(q);
		
		CannedModelResolver resolver = new CannedModelResolver(reply);
		Deny deny = h -> h.contains("mozaws.net");
		Allow allow = h -> false;
		Block block = h -> false;

		var sut = new LookupEngine(resolver, block, deny, allow);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.DENIED);
	}

	/**
	 * An allowed query (or element in c-name chain) should
	 * return resolved IP, even if the query/chain also contains
	 * denied entries.
	 * Note that a cache/upstream resolve is always needed (the
	 * IP is needed, after all).
	 * 
	 * Use example of detectportal.firefox.com which replies
	 * 	 detectportal.firefox.com.       11      IN      CNAME                                                    
	 *   detectportal.prod.mozaws.net.   60      IN      CNAME                                                    
	 *   detectportal.firefox.com-v2.edgesuite.net 9999 IN CNAME
     *   a1089.dscd.akamai.net.  10      IN      A       95.101.142.120                                           
     *   a1089.dscd.akamai.net.  10      IN      A       104.84.152.177
	 */
	@Test
	public void allowedEntriesShouldBeResolved() throws UnknownHostException {
		Query q = makeTestQuery(DETECTPORTAL_FIREFOX_COM);
		DnsReply reply = getDetectportalFirefoxChainedReply(q);
		
		CannedModelResolver resolver = new CannedModelResolver(reply);
		Deny deny = h -> h.contains("mozaws.net");
		Allow allow = h -> h.contains("akamai.net");
		Block block = h -> false;

		var sut = new LookupEngine(resolver, block, deny, allow);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.ALLOWED);
	}

	/**
	 * Same test as allowedEntriesShouldBeResolved, but query
	 * is denied, overriding the allow state.
	 */
	@Test
	public void denyTrumpsAllowInQuery() throws UnknownHostException {
		Query q = makeTestQuery(DETECTPORTAL_FIREFOX_COM);
		DnsReply reply = getDetectportalFirefoxChainedReply(q);
		
		CannedModelResolver resolver = new CannedModelResolver(reply);
		Deny deny = h -> h.contains("firefox.com");
		Allow allow = h -> h.contains("akamai.net");
		Block block = h -> false;

		var sut = new LookupEngine(resolver, block, deny, allow);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.DENIED);
	}
	
	/**
	 * If query has not been affected by allow or deny.
	 * it will be checked against blocked list from external.
	 */
	@Test
	public void blockageOnlyIfOtherListsDidNotTrigger() throws UnknownHostException {
		Query q = makeTestQuery(DETECTPORTAL_FIREFOX_COM);
		DnsReply reply = getDetectportalFirefoxChainedReply(q);
		
		CannedModelResolver resolver = new CannedModelResolver(reply);
		Deny deny = h -> false;
		Allow allow = h -> false;
		Block block = h -> h.contains("akamai");

		var sut = new LookupEngine(resolver, block, deny, allow);
		LookupResult result = sut.lookup(q);

		assertThat(result.getState())
			.isEqualTo(LookupState.BLOCKED);
	}

	/**
	 * If none of the lists affect a query, it should be returned.
	 */
	@Test
	public void unfilteredRepliesAreReturned() throws UnknownHostException {
		Query q = makeTestQuery(DETECTPORTAL_FIREFOX_COM);
		DnsReply reply = getDetectportalFirefoxChainedReply(q);
		
		CannedModelResolver resolver = new CannedModelResolver(reply);
		Deny deny = h -> false;
		Allow allow = h -> false;
		Block block = h -> false;

		var sut = new LookupEngine(resolver, block, deny, allow);
		LookupResult result = sut.lookup(q);
		
		assertThat(result.getState())
			.isEqualTo(LookupState.PASSTHROUGH);
	}

	/**
	 * All lookups (including allowed and denied) should
	 * be cached, observing TTL.
	 * The cache should be preferred to upstream lookup.
	 */
	@Test
	public void resolvedRepliesShouldBeCached() {
		// TODO: write test
	}
}
