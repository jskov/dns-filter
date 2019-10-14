package dk.mada.dns.wire.model;

/**
 * The domain model reply from a DNS request.
 */
public class DnsReply extends DnsMessage {
	private DnsHeaderReply header;

	DnsReply(DnsHeaderReply header, DnsSection request) {
		super(request);
		this.header = header;
	}

	@Override
	public DnsHeaderReply getHeader() {
		return header;
	}
	
	@Override
	public String toString() {
		return "DnsReply [getQuestion()=" + getQuestion() + ", getAnswer()=" + getAnswer() + ", getAuthority()="
				+ getAuthority() + "]";
	}

}
