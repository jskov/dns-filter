package dk.mada.dns.wire.model;

import java.util.List;

/**
 * DNS section of answer records.
 */
public class DnsSectionAnswer extends DnsSection {
	DnsSectionAnswer(List<DnsRecord> records) {
		super(DnsSectionType.ANSWER, records);
	}
}
