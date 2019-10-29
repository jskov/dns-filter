package dk.mada.dns.wire.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An OPT record, as per https://tools.ietf.org/html/rfc6891
 */
public class DnsRecordOpt extends DnsRecord {
	private final byte xrcode;
	private final byte version;
	private final short flags;
	
	/** Requstor's max payload size - allows bigger replies than 512 */
	private final short payloadSize;
	private final List<DnsOption> options;

	DnsRecordOpt(DnsName name, DnsRecordType type, short payloadSize, byte xrcode,  byte version, short flags, List<DnsOption> options) {
		super(DnsClass.IN, type, name, 0);
		
		this.xrcode = xrcode;
		this.version = version;
		this.flags = flags;
		this.payloadSize = payloadSize;
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

	public short getFlags() {
		return flags;
	}

	public byte getXrcode() {
		return xrcode;
	}

	public byte getVersion() {
		return version;
	}

	public short getPayloadSize() {
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
