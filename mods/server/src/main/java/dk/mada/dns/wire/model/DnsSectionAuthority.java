package dk.mada.dns.wire.model;

import java.util.List;

/**
 * DNS section of authority records.
 */
public class DnsSectionAuthority extends DnsSection {
	DnsSectionAuthority(List<DnsRecord> records) {
		super(DnsSectionType.AUTHORITY, records);
	}
}
