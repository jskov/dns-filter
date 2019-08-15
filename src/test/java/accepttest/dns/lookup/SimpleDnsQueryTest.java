package accepttest.dns.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.UnknownHostException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class SimpleDnsQueryTest {
	/**
	 * Simple DNS query test which expects a reply
	 * from looking up github.com
	 */
    @Test
    public void testDnsLookup() throws TextParseException, UnknownHostException {
    	Lookup lookup = new Lookup("github.com");
    	lookup.setResolver(getLocalhostResolver());
    	lookup.setCache(null);
    	lookup.setSearchPath(new String[] {});
  
    	Record[] res = lookup.run();
    	assertThat(lookup.getResult())
    		.isEqualTo(0);
    	assertThat(res)
    		.extracting(r -> r.getName().toString())
    		.contains("github.com.");
    }

	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("127.0.0.1");
    	localhostResolver.setPort(8053);
    	return localhostResolver;
	}
}