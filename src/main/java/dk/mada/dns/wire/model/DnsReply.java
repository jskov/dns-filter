package dk.mada.dns.wire.model;

/**
 * The domain model reply from a DNS request.
 */
public class DnsReply extends DnsMessage {

	private DnsReply(DnsHeader header, DnsSection request) {
		super(header, request);
	}
	
	public static DnsReply fromAnswer(DnsHeader header, DnsSection question, DnsSection answer) {
		var res = new DnsReply(header, question);
		res.setAnswer(answer);
		return res;
	}

	@Override
	public String toString() {
		return "DnsReply [getQuestion()=" + getQuestion() + ", getAnswer()=" + getAnswer() + ", getAuthority()="
				+ getAuthority() + "]";
	}
}
