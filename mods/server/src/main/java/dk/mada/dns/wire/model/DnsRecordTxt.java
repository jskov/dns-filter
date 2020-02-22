package dk.mada.dns.wire.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A TXT record, as per https://tools.ietf.org/html/rfc1464
 * 
 * Just keeps txt entries as strings for now. When doing decoder, introduce a key-value type.
 */
public class DnsRecordTxt extends DnsRecord {
	private List<String> txts;
	
	DnsRecordTxt(DnsName name, DnsClass dnsClass, long ttl, List<String> txts) {
		super(dnsClass, DnsRecordType.TXT, name, ttl);
		
		this.txts = txts;
	}
	
	@Override
	public void ifRecordTxt(Consumer<DnsRecordTxt> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordTxt> asRecordTxt() {
		return Optional.of(this);
	}

	public List<String> getTxts() {
		return txts;
	}
}
