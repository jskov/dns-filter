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
	
	public static DnsSection make(DnsSectionType type, List<DnsRecord> records) {
		return new DnsSection(type, records);
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
}
