package test.dns.conversion;

import static fixture.dns.wiredata.TestQueries.makeTestQuery;

import org.junit.jupiter.api.Test;

import dk.mada.dns.lookup.Query;
import static org.assertj.core.api.Assertions.assertThat;

class DnsKeyTest {
	/* Bad handling: Failed to convert request to model                                                                                                            
	* 0x0000 d4 99 01 10 00 01 00 00  00 00 00 01 00 00 30 00 ..............0.                                                                                     
	* 0x0010 01 00 00 29 04 d0 00 00  80 00 00 00             ...)........                                                                                         
	 */                                                                                                                                                            
	private static final byte[] REQUEST = new byte[] {(byte)0xd4, (byte)0x99, 0x01, 0x10, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x30, 0x00, 0x01, 0x00, 0x00, 0x29, 0x04, (byte)0xd0, 0x00, 0x00, (byte)0x80, 0x00, 0x00, 0x00, };

	/**
	 * Just a simple test to see that request can be parsed.
	 * No further action implemented.
	 */
	@Test
	void doesNotExplodeOnTypeDecoding() {
		Query q = makeTestQuery(REQUEST);
		
		assertThat(q)
			.isNotNull();
	}
}
