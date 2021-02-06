package dk.mada.dns.wire.model;

import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecordHttps extends DnsRecord {
	private final DnsName targetName;
	
	DnsRecordHttps(DnsName name, DnsName targetName, long ttl) {
		super(DnsClass.IN, DnsRecordType.HTTPS, name, ttl);
		
		this.targetName = targetName;
	}
	
	@Override
	public void ifRecordHttps(Consumer<DnsRecordHttps> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordHttps> asRecordHttps() {
		return Optional.of(this);
	}
	
	public DnsName getTarget() {
		return targetName;
	}
}
