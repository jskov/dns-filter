package dk.mada.dns.wire.model;

import java.net.InetAddress;
import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecordA extends DnsRecord {
	private final InetAddress address;
	
	DnsRecordA(DnsName name, InetAddress address, long ttl) {
		super(DnsClass.IN, DnsRecordType.A, name, ttl);
		
		this.address = address;
	}
	
	@Override
	public void ifRecordA(Consumer<DnsRecordA> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordA> asRecordA() {
		return Optional.of(this);
	}
	
	public InetAddress getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "DnsRecordA [address=" + address + ", getDnsClass()=" + getDnsClass() + ", getRecordType()="
				+ getRecordType() + ", getName()=" + getName() + ", getTtl()=" + getTtl() + "]";
	}
}
