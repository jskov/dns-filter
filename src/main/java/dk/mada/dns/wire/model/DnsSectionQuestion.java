package dk.mada.dns.wire.model;

import java.util.List;

/**
 * DNS section of question record.
 */
public class DnsSectionQuestion extends DnsSection {
	DnsSectionQuestion(DnsRecord record) {
		super(DnsSectionType.QUESTION, List.of(record));
	}
}
