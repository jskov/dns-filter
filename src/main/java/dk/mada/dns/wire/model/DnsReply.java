package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

/**
 * The domain model reply from a DNS request.
 */
public class DnsReply extends DnsMessage {
	private DnsHeaderReply header;
	private ByteBuffer optWireReply;

	DnsReply(DnsHeaderReply header, DnsSection request) {
		super(request);
		this.header = header;
	}

	@Override
	public DnsHeaderReply getHeader() {
		return header;
	}
	
	public ByteBuffer getOptWireReply() {
		return optWireReply;
	}

	public void setOptWireReply(ByteBuffer optWireReply) {
		this.optWireReply = optWireReply;
	}

	@Override
	public String toString() {
		return "DnsReply [getQuestion()=" + getQuestion() + ", getAnswer()=" + getAnswer() + ", getAuthority()="
				+ getAuthority() + "]";
	}

}
