package dk.mada.dns.wire.model;

/**
 * The domain model reply from a DNS request.
 */
public class DnsReply extends DnsMessage {
	DnsReply(DnsHeader header, DnsSection request) {
		super(header, request);
	}
	
	@Override
	public String toString() {
		return "DnsReply [getQuestion()=" + getQuestion() + ", getAnswer()=" + getAnswer() + ", getAuthority()="
				+ getAuthority() + "]";
	}
}
