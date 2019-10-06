package dk.mada.dns.wire.model;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecordA extends DnsRecord {
	private final InetAddress address;
	
	private DnsRecordA(DnsName name, InetAddress address, long ttl) {
		super(DnsClass.IN, DnsRecordType.A, name, ttl);
		
		this.address = address;
	}
	
	public static DnsRecordA from(DnsName name, InetAddress address, long ttl) {
		return new DnsRecordA(name, address, ttl);
	}

	public static DnsRecordA blindFrom(DnsName name, long ttl) {
		try {
			return new DnsRecordA(name, InetAddress.getByAddress(new byte[] { 0, 0, 0, 0}), ttl);
		} catch (UnknownHostException e) {
			throw new UncheckedIOException(e);
		}
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
