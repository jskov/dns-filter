package accepttest.dns.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dk.mada.dns.wire.model.DnsReply;
import fixture.dns.xbill.DnfFilterLocalHostLookup;
import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class SimpleDnsQueryTest {
	@Inject private DnfFilterLocalHostLookup dnsFilterLookup;

	/**
	 * Simple DNS query test which expects a reply
	 * from looking up github.com
	 */
    @Test
    public void testDnsLookup() {
    	DnsReply reply = dnsFilterLookup.serviceDnsLookup("mada.dk");
    			
    	assertThat(reply.getAnswer().getRecords())
    		.allSatisfy(dr -> {
    			assertThat(dr.getName().getName()).contains("mada.dk");
    			assertThat(dr.asRecordA())
    				.get()
    				.extracting(ar -> ar.getAddress().getHostAddress())
    				.isEqualTo("85.203.223.39");
    		});
    }
    
    @Test
    public void testIpv6NoAnswerLookup() {
    	DnsReply reply = dnsFilterLookup.serviceDnsLookupIpv6("mada.dk");
    			
    	assertThat(reply.getAnswer().getRecords())
    		.isEmpty();
    }
    
}
