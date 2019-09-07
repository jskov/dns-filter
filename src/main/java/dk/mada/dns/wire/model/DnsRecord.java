package dk.mada.dns.wire.model;

import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecord {
	private DnsClass dnsClass;
	private DnsRecordType recordType;
	private DnsName name;
	private long ttl;

	protected DnsRecord(DnsClass dnsClass, DnsRecordType recordType, DnsName name, long ttl) {
		super();
		this.dnsClass = dnsClass;
		this.recordType = recordType;
		this.name = name;
		this.ttl = ttl;
	}
	
	public static DnsRecord unknownFrom(DnsRecordType recordType, DnsName name, long ttl) {
		return new DnsRecord(DnsClass.IN, recordType, name, ttl);
	}

	public DnsClass getDnsClass() {
		return dnsClass;
	}

	public void setDnsClass(DnsClass dnsClass) {
		this.dnsClass = dnsClass;
	}

	public DnsRecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(DnsRecordType recordType) {
		this.recordType = recordType;
	}

	public DnsName getName() {
		return name;
	}

	public void setName(DnsName name) {
		this.name = name;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	public void ifRecordA(Consumer<DnsRecordA> c) {
	}
	
	public Optional<DnsRecordA> asRecordA() {
		return Optional.empty();
	}
	
	@Override
	public String toString() {
		return "DnsRecord [recordType=" + recordType + ", name=" + name + ", ttl=" + ttl
				+ "]";
	}
}
