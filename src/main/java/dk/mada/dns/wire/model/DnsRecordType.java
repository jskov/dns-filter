package dk.mada.dns.wire.model;

/**
 * Dns record type.
 * 
 * From https://www.ietf.org/rfc/rfc1035.txt, section 3.2.2. TYPE values
 */
public enum DnsRecordType {
	A(1), 		// host address
	NS(2),		// authoritative nameserver
	CNAME(5),	// c-name, alias
	MX(15),
	TXT(16),
	AAAA(28),	// AAAA IPV6 https://tools.ietf.org/html/rfc3596
	OPT(41);	// OPT, https://tools.ietf.org/html/rfc6891
	
	private final int wireValue;
	
	private DnsRecordType(int wireValue) {
		this.wireValue = wireValue;
	}

	public int getWireValue() {
		return wireValue;
	}
	
	public static DnsRecordType fromWireValue(int wireValue) {
		for (DnsRecordType t : values()) {
			if (t.wireValue == wireValue) {
				return t;
			}
		}
		throw new IllegalArgumentException("Unknown DNS record type with value " + wireValue);
	}
}
