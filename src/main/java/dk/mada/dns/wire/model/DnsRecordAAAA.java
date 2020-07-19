package dk.mada.dns.wire.model;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecordAAAA extends DnsRecord {
	private final Inet6Address address;
	
	DnsRecordAAAA(DnsName name, InetAddress address, long ttl) {
		super(DnsClass.IN, DnsRecordType.AAAA, name, ttl);
		
		if (!(address instanceof Inet6Address)) {
			throw new IllegalArgumentException("DnsRecordAAAA only takes IPv6 addresses, got " + address);
		}
		this.address = (Inet6Address)address;
	}
	
	@Override
	public void ifRecordAAAA(Consumer<DnsRecordAAAA> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordAAAA> asRecordAAAA() {
		return Optional.of(this);
	}
	
	public Inet6Address getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "DnsRecordAAAA [address=" + address + ", getDnsClass()=" + getDnsClass() + ", getRecordType()="
				+ getRecordType() + ", getName()=" + getName() + ", getTtl()=" + getTtl() + "]";
	}
}
