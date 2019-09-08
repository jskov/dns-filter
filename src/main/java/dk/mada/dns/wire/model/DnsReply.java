package dk.mada.dns.wire.model;

/**
 * The domain model reply from a DNS request.
 */
public class DnsReply extends DnsMessage {

	private DnsReply(DnsSection request) {
		super(request);
	}
	
	public static DnsReply fromAnswer(DnsSection question, DnsSection answer) {
		var res = new DnsReply(question);
		res.setAnswer(answer);
		return res;
	}

	@Override
	public String toString() {
		return "DnsReply [getQuestion()=" + getQuestion() + ", getAnswer()=" + getAnswer() + ", getAuthority()="
				+ getAuthority() + "]";
	}
}
