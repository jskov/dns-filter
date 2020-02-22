package dk.mada.dns.wire.model;

import java.util.Optional;
import java.util.function.Consumer;

public class DnsRecordC extends DnsRecord {
	private final DnsName aliasName;
	
	DnsRecordC(DnsName name, DnsName aliasName, long ttl) {
		super(DnsClass.IN, DnsRecordType.CNAME, name, ttl);
		
		this.aliasName = aliasName;
	}
	
	@Override
	public void ifRecordC(Consumer<DnsRecordC> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordC> asRecordC() {
		return Optional.of(this);
	}
	
	public DnsName getAlias() {
		return aliasName;
	}
}
