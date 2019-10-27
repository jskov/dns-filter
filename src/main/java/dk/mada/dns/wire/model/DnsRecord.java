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

	public void ifRecordAAAA(Consumer<DnsRecordAAAA> c) {
	}

	public Optional<DnsRecordAAAA> asRecordAAAA() {
		return Optional.empty();
	}

	public void ifRecordC(Consumer<DnsRecordC> c) {
	}
	
	public Optional<DnsRecordC> asRecordC() {
		return Optional.empty();
	}

	public void ifRecordQ(Consumer<DnsRecordQ> c) {
	}

	public Optional<DnsRecordQ> asRecordQ() {
		return Optional.empty();
	}

	public void ifRecordOpt(Consumer<DnsRecordOpt> c) {
	}

	public Optional<DnsRecordOpt> asRecordOpt() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return "DnsRecord [recordType=" + recordType + ", name=" + name + ", ttl=" + ttl
				+ "]";
	}
}
