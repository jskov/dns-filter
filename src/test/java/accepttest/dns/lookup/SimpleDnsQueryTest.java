package accepttest.dns.lookup;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;

import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class SimpleDnsQueryTest {
	/**
	 * Simple DNS query test which expects a reply
	 * from looking up github.com
	 */
	@Disabled("Because it is not implemented yet")
    @Test
    public void testDnsLookup() throws TextParseException, UnknownHostException {
    	Lookup lookup = new Lookup("github.com");
    	SimpleResolver localhostResolver = new SimpleResolver("127.0.0.1");
    	localhostResolver.setPort(8053);
		lookup.setResolver(localhostResolver);
    	lookup.setCache(null);
    	
    	Record[] res = lookup.run();
    	for (Record r : res) {
    		System.out.println("" + r);
    	}
    	assertThat(res)
    		.isNotEmpty();
   }
}