package dk.mada.dns.wire.model;

import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecordSVCB extends DnsRecord {
	private final DnsName targetName;
	
	DnsRecordSVCB(DnsName name, DnsName targetName, long ttl) {
		super(DnsClass.IN, DnsRecordType.SVCB, name, ttl);
		
		this.targetName = targetName;
	}
	
	@Override
	public void ifRecordSVCB(Consumer<DnsRecordSVCB> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordSVCB> asRecordSVCB() {
		return Optional.of(this);
	}
	
	public DnsName getTarget() {
		return targetName;
	}
}
