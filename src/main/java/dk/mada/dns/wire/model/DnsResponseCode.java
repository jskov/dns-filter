package dk.mada.dns.wire.model;

/**
 * Dns reply response code, as per https://www.ietf.org/rfc/rfc1035.txt, 4.1.1. Header section format
 */
public enum DnsResponseCode {
	NOERR(0),
	FORMAT_ERROR(1),
	SERVER_FAILURE(2),
	NXDOMAIN(3),
	NOT_IMPLEMENTED(4),
	REFUSED(5);
	
	private final int code;
	
	private DnsResponseCode(int code) {
		this.code = code;
	}
	
	public static DnsResponseCode fromWire(short flags) {
		int v = flags & 0x000f;
		for (DnsResponseCode rc : values()) {
			if (rc.code == v) {
				return rc;
			}
		}
		throw new IllegalArgumentException("Unknown response code " + v);
	}
}
