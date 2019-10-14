package dk.mada.dns.wire.model;

import dk.mada.dns.util.Hexer;

public class DnsHeaderReply extends DnsHeader {
	DnsHeaderReply(short id, short flags, short qdcount, short ancount, short nscount, short arcount) {
		super(id, flags, qdcount, ancount, nscount, arcount);
		
		if ((flags & FLAGS_QR) == 0) {
			throw new IllegalStateException("Not a reply header with flags " + Hexer.hexShort(flags));
		}
	}
}
