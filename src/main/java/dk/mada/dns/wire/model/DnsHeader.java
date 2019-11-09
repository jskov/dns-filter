package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

import dk.mada.dns.util.Hexer;

/**
 * DNS message header.
 * 
 * From https://www.ietf.org/rfc/rfc1035.txt, 4.1.1. Header section format
 */
public abstract class DnsHeader {
	// Header flags overview https://www.iana.org/assignments/dns-parameters/dns-parameters.xhtml
	public static final short FLAGS_QR = (short)0x8000; // response when set
	public static final int FLAGS_AA = 0x0400; // Authoritative Answer
	public static final int FLAGS_TC = 0x0200; // TrunCation
	public static final int FLAGS_RD = 0x0100; // Recursion Desired
	public static final int FLAGS_RA = 0x0080; // Recursion Available
	public static final int FLAGS_Z =  0x0040; // Reserved
	public static final int FLAGS_AD = 0x0020; // Authentic data https://tools.ietf.org/html/rfc6895, https://tools.ietf.org/html/rfc6840#section-5.7
	public static final int FLAGS_CD = 0x0010; // Checking Disabled

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
	
	public DnsResponseCode getResponseCode() {
		return DnsResponseCode.fromWire(flags);
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
	
	public byte[] toWireFormat() {
		var bb = ByteBuffer.allocate(12);
		bb.putShort(id);
		bb.putShort(flags);
		bb.putShort(qdcount);
		bb.putShort(ancount);
		bb.putShort(nscount);
		bb.putShort(arcount);
		
		return bb.array();
	}

	/**
	 * Prepare for wire, but zero out parts that xbill will later add to.
	 * @return
	 */
	public byte[] toWireFormatZeroForReply() {
		var bb = ByteBuffer.allocate(12);
		bb.putShort(id);
		bb.putShort(flags);
		bb.putShort(qdcount);
		bb.putShort((short)0);
		bb.putShort(nscount);
		bb.putShort((short)0);
		
		return bb.array();
	}

	public String toDebugString() {
		String qr = ((flags & 0x8000) == 0) ? "Q" : "R";
		int rcode = flags & 0x000f;
		String status = rcode == 0 ? "OK" : ("ERR(" + String.format("0x%01x", rcode) + ")");
		
		String aa = ((flags & FLAGS_AA) != 0) ? "aa" : "";
		String tc = ((flags & FLAGS_TC) != 0) ? "tc" : "";
		String rd = ((flags & FLAGS_RD) != 0) ? "rd" : "";
		String ra = ((flags & FLAGS_RA) != 0) ? "ra" : "";
		String ad = ((flags & FLAGS_AD) != 0) ? "ad" : "";
		String cd = ((flags & FLAGS_CD) != 0) ? "cd" : "";
		int opcode = ((flags >> 11) & 0x000f);
		String opc = "?";
		if (opcode == 0) {
			opc = "query";
		} else if (opcode == 1) {
			opc = "iquery";
		} else if (opcode == 2) {
			opc = "status";
		}
		
		return new StringBuilder()
				.append("id:").append(Hexer.hexShort(id))
				.append(", flags:").append(Hexer.hexShort(flags))
				.append(" (")
				.append(qr).append(",")
				.append(opc).append(",")
				.append(aa).append(",")
				.append(tc).append(",")
				.append(rd).append(",")
				.append(ra).append(",")
				.append(ad).append(",")
				.append(cd).append(",")
				.append(status)
				.append(")")
				.toString();
	}
	
	@Override
	public String toString() {
		return toDebugString();
	}
}
