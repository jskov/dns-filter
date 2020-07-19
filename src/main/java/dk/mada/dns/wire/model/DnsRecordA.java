package dk.mada.dns.wire.model;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecordA extends DnsRecord {
	private final Inet4Address address;
	
	DnsRecordA(DnsName name, InetAddress address, long ttl) {
		super(DnsClass.IN, DnsRecordType.A, name, ttl);
		
		if (!(address instanceof Inet4Address)) {
			throw new IllegalArgumentException("DnsRecordA only takes IPv4 addresses, got " + address);
		}

		this.address = (Inet4Address)address;
	}
	
	@Override
	public void ifRecordA(Consumer<DnsRecordA> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordA> asRecordA() {
		return Optional.of(this);
	}
	
	public Inet4Address getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "DnsRecordA [address=" + address + ", getDnsClass()=" + getDnsClass() + ", getRecordType()="
				+ getRecordType() + ", getName()=" + getName() + ", getTtl()=" + getTtl() + "]";
	}
}
