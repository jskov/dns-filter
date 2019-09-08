package dk.mada.dns.wire.model;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * A question record, as per https://www.ietf.org/rfc/rfc1035.txt, 4.1.2. Question section format
 * Simplified with canned types for now - not sure if needed.
 */
public class DnsRecordQ extends DnsRecord {
	private DnsRecordQ(DnsName name) {
		super(DnsClass.IN, DnsRecordType.A, name, 0);
	}
	
	public static DnsRecordQ from(DnsName name) {
		return new DnsRecordQ(name);
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
