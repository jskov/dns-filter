package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

public class DnsHeaderQueries {
	public static DnsHeaderQuery newObsoleted(short id, short flags, short qdcount, short ancount, short nscount, short arcount) {
		return new DnsHeaderQuery(id, flags, qdcount, ancount, nscount, arcount);
	}
	
	public static DnsHeaderQuery fromRequest(ByteBuffer req) {
		req.rewind();
		var res = new DnsHeaderQuery(req.getShort(), req.getShort(), req.getShort(), req.getShort(), req.getShort(), req.getShort());
		req.rewind();
		return res;
	}
}
