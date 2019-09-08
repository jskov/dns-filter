package dk.mada.dns.wire.model;

/**
 * DNS message header.
 * 
 * From https://www.ietf.org/rfc/rfc1035.txt, 4.1.1. Header section format
 */
class DnsHeader {
	private final short id;
	private final short flags;
	private final short qdcount;
	private final short ancount;
	private final short nscount;
	private final short arcount;

	protected DnsHeader(short id, short flags, short qdcount, short ancount, short nscount, short arcount) {
		this.id = id;
		this.flags = flags;
		this.qdcount = qdcount;
		this.ancount = ancount;
		this.nscount = nscount;
		this.arcount = arcount;
	}

	public short getId() {
		return id;
	}

	public short getFlags() {
		return flags;
	}

	public short getQdcount() {
		return qdcount;
	}

	public short getAncount() {
		return ancount;
	}

	public short getNscount() {
		return nscount;
	}

	public short getArcount() {
		return arcount;
	}
}
