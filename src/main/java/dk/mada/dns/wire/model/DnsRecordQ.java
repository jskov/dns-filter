package dk.mada.dns.wire.model;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * A question record, as per https://www.ietf.org/rfc/rfc1035.txt, 4.1.2. Question section format
 */
public class DnsRecordQ extends DnsRecord {
	DnsRecordQ(DnsName name, DnsRecordType type) {
		super(DnsClass.IN, type, name, 0);
	}
	
	@Override
	public void ifRecordQ(Consumer<DnsRecordQ> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordQ> asRecordQ() {
		return Optional.of(this);
	}

	@Override
	public String toString() {
		return "DnsRecordQ [getRecordType()=" + getRecordType() + ", getName()=" + getName() + "]";
	}
}
