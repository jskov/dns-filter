package accepttest.dns.lookup;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class SimpleDnsQueryTest {
	private static final Logger logger = LoggerFactory.getLogger(SimpleDnsQueryTest.class);
	
	@BeforeAll
	static void info() throws UnknownHostException {
    	InetAddress lh = InetAddress.getLocalHost();
    	logger.info("HOST {} : {} : {}", lh.getHostName(), lh.getHostAddress(),  InetAddress.getLoopbackAddress());
	}
	
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
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
    	localhostResolver.setPort(8053);
    	return localhostResolver;
	}
}