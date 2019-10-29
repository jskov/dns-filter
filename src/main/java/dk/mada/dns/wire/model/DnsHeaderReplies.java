package dk.mada.dns.wire.model;

public class DnsHeaderReplies {
	public static DnsHeaderReply fromWire(short id, short flags, short qdcount, short ancount, short nscount, short arcount) {
		return new DnsHeaderReply(id, flags, qdcount, ancount, nscount, arcount);
	}

	public static DnsHeaderReply fromRequest(DnsHeaderQuery r, short ancount, short nscount, short arcount) {
		return new DnsHeaderReply(r.getId(), (short)(r.getFlags() | 0x8000), r.getQdcount(), ancount, nscount, arcount);
	}

}
