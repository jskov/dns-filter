package dk.mada.dns.wire.model;

/**
 * DNS Class.
 * 
 * From https://www.ietf.org/rfc/rfc1035.txt section 3.2.4. CLASS values
 */
public enum DnsClass {
	IN(1),
	CS(2),
	CH(3),
	HS(4);
	
	private final int wireValue;
	
	private DnsClass(int wireValue) {
		this.wireValue = wireValue;
	}

	public int getWireValue() {
		return wireValue;
	}
}
