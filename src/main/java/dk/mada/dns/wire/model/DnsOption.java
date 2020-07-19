package dk.mada.dns.wire.model;

public abstract class DnsOption {
	public static final int DNS_OPTION_COOKIE = 10;

	final short code;

	public DnsOption(short code) {
		this.code = code;
	}

	public short getCode() {
		return code;
	}
}
