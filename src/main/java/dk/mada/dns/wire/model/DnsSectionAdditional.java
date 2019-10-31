package dk.mada.dns.wire.model;

import java.util.List;

/**
 * DNS section of additional records.
 */
public class DnsSectionAdditional extends DnsSection {
	DnsSectionAdditional(List<DnsRecord> records) {
		super(DnsSectionType.ADDITIONAL, records);
	}
}
