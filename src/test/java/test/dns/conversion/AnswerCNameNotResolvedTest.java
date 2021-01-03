package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.OLD_REDDIT_REPLY;
import static fixture.dns.wiredata.TestQueries.OLD_REDIT_REQUEST;
import static fixture.dns.wiredata.TestQueries.makeTestQuery;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import dk.mada.dns.filter.Allow;
import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.Deny;
import dk.mada.dns.lookup.LookupEngine;
import dk.mada.dns.lookup.LookupResult;
import dk.mada.dns.lookup.Query;
import dk.mada.dns.resolver.Resolver;
import dk.mada.dns.wire.model.DnsRecordA;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsResponseCode;
import fixture.resolver.CannedUdpResolver;

/**
 * Regression fix for old.reddit.com which upstream returns as:
 * 
 *   ;; QUESTION SECTION:
 *   ;old.reddit.com.			IN	A
 *   
 *   ;; ANSWER SECTION:
 *   old.reddit.com.		299	IN	CNAME	reddit.map.fastly.net.
 *   reddit.map.fastly.net.	29	IN	A	151.101.65.140
 *   reddit.map.fastly.net.	29	IN	A	151.101.193.140
 *   reddit.map.fastly.net.	29	IN	A	151.101.129.140
 *   reddit.map.fastly.net.	29	IN	A	151.101.1.140
 *
 * But which was returned from lookup engine as:
 * 
 *   ;old.reddit.com.			IN	A
 *   
 *   ;; ANSWER SECTION:
 *   old.reddit.com.		288	IN	CNAME	old.reddit.com.
 *   reddit.map.fastly.net.	18	IN	A	151.101.1.140
 *   reddit.map.fastly.net.	18	IN	A	151.101.65.140
 *   reddit.map.fastly.net.	18	IN	A	151.101.129.140
 *   reddit.map.fastly.net.	18	IN	A	151.101.193.140
 * 
 * Causing infinite recursion in clients.
 * 
 */
//@QuarkusTest
public class AnswerCNameNotResolvedTest {
	@Test
	public void doesNotResolveAnswerCTargets() {
		Query q = makeTestQuery(OLD_REDIT_REQUEST);

		Resolver resolver = new CannedUdpResolver(OLD_REDDIT_REPLY);
		Deny deny = h -> false;
		Allow allow = h -> false;
		Block block = h -> false;
		
		var sut = new LookupEngine(resolver, allow, deny, block);
		LookupResult result = sut.lookup(q);

		DnsReply reply = result.getReply();
		DnsResponseCode responseCode = reply.getHeader().getResponseCode();
		
		assertThat(responseCode)
			.isEqualTo(DnsResponseCode.NOERR);
		
		List<String> txtRecord = reply.getAnswer().getRecords().stream()
				.map(r -> r.getRecordType().name() + ":" + r.getName().getName() + (r.isA() ? (":" +((DnsRecordA)r).getAddress()) : ""))
				.collect(toList());
		
		assertThat(txtRecord)
			.containsExactly(
					"CNAME:reddit.map.fastly.net",
					"A:reddit.map.fastly.net:reddit.map.fastly.net./151.101.1.140",
					"A:reddit.map.fastly.net:reddit.map.fastly.net./151.101.65.140",
					"A:reddit.map.fastly.net:reddit.map.fastly.net./151.101.129.140",
					"A:reddit.map.fastly.net:reddit.map.fastly.net./151.101.193.140"
				);
	}
}
