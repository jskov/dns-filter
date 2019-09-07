package accepttest.dns.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;

import javax.inject.Inject;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.wire.model.DnsReply;
import fixture.dns.DnsPayloadHelper;
import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class SimpleDnsQueryTest {
	@Inject private DnsPayloadHelper dnsHelper;

	/**
	 * Simple DNS query test which expects a reply
	 * from looking up github.com
	 */
    @Test
    public void testDnsLookup() throws TextParseException, UnknownHostException {
    	DnsReply reply = dnsHelper.serviceDnsLookup("mada.dk");
    			
    	assertThat(reply.getAnswer().getRecords())
    		.allSatisfy(dr -> {
    			assertThat(dr.getName().getName()).contains("mada.dk");
    			assertThat(dr.asRecordA())
    				.get()
    				.extracting(ar -> ar.getAddress().getHostAddress())
    				.isEqualTo("185.17.217.100");
    		});
    }
}
