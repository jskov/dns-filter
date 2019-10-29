package dk.mada.dns.wire.model;

/**
 * DNS client cookie, as per https://tools.ietf.org/html/rfc7873
 */
public class DnsOptionCookie extends DnsOption {
	public DnsOptionCookie() {
		super((short)DNS_OPTION_COOKIE);
	}
}
