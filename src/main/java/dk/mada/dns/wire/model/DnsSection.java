package dk.mada.dns.wire.model;

import java.util.List;
import java.util.stream.Stream;

public abstract class DnsSection {
	private DnsSectionType type;
	private List<DnsRecord> records;
	
	DnsSection(DnsSectionType type, List<DnsRecord> records) {
		this.type = type;
		this.records = records;
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
