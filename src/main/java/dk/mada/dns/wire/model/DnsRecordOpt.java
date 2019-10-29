package dk.mada.dns.wire.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An OPT record, as per https://tools.ietf.org/html/rfc6891
 */
public class DnsRecordOpt extends DnsRecord {
	private final int flags;
	/** Requstor's max payload size - allows bigger replies than 512 */
	private final int payloadSize;
	private final List<DnsOption> options;

	DnsRecordOpt(DnsName name, DnsRecordType type, int payloadSize, int flags, List<DnsOption> options) {
		super(DnsClass.IN, type, name, 0);
		
		this.payloadSize = payloadSize;
		this.flags = flags;
		this.options = options;
	}
	
	@Override
	public void ifRecordOpt(Consumer<DnsRecordOpt> c) {
		c.accept(this);
	}

	@Override
	public Optional<DnsRecordOpt> asRecordOpt() {
		return Optional.of(this);
	}

	public int getFlags() {
		return flags;
	}

	public int getPayloadSize() {
		return payloadSize;
	}

	public List<DnsOption> getOptions() {
		return options;
	}

	@Override
	public String toString() {
		return "DnsRecordOpt [flags=" + flags + ", payloadSize=" + payloadSize + ", options=" + options + "]";
	}
}
