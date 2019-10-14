package dk.mada.dns.wire.model;

import java.util.List;

public class DnsSections {
	public static DnsSection from(DnsSectionType type, List<DnsRecord> records) {
		return new DnsSection(type, records);
	}

	public static DnsSection ofQuestion(DnsRecord record) {
		return new DnsSection(DnsSectionType.QUESTION, List.of(record));
	}

	public static DnsSection ofAnswers(List<DnsRecord> records) {
		return new DnsSection(DnsSectionType.ANSWER, records);
	}

	public static DnsSection ofAnswers(DnsRecord... records) {
		return new DnsSection(DnsSectionType.ANSWER, List.of(records));
	}
}
