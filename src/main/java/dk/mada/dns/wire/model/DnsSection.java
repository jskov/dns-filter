package dk.mada.dns.wire.model;

import java.util.List;
import java.util.stream.Stream;

public class DnsSection {
	private DnsSectionType type;
	private List<DnsRecord> records;
	
	private DnsSection(DnsSectionType type, List<DnsRecord> records) {
		this.type = type;
		this.records = records;
	}
	
	public static DnsSection from(DnsSectionType type, List<DnsRecord> records) {
		return new DnsSection(type, records);
	}

	public static DnsSection ofQuestion(DnsRecord record) {
		return new DnsSection(DnsSectionType.QUESTION, List.of(record));
	}

	public static DnsSection ofAnswers(List<DnsRecord> records) {
		return new DnsSection(DnsSectionType.ANSWER, records);
	}

	
	public DnsSectionType getType() {
		return type;
	}

	public List<DnsRecord> getRecords() {
		return records;
	}
	
	public Stream<DnsRecord> stream() {
		return records.stream();
	}

	@Override
	public String toString() {
		return "DnsSection [type=" + type + ", records=" + records + "]";
	}
}
