package dk.mada.dns.wire.model;

import java.util.List;

public class DnsSections {
	public static DnsSectionQuestion ofQuestion(DnsRecord record) {
		return new DnsSectionQuestion(record);
	}

	public static DnsSectionAnswer ofAnswers(List<DnsRecord> records) {
		return new DnsSectionAnswer(records);
	}

	public static DnsSectionAnswer ofAnswers(DnsRecord... records) {
		return new DnsSectionAnswer(List.of(records));
	}

	public static DnsSectionAdditional ofAdditionals(List<DnsRecord> records) {
		return new DnsSectionAdditional(records);
	}

	public static DnsSectionAdditional emptyAdditionals() {
		return new DnsSectionAdditional(List.of());
	}
}
