package dk.mada.dns.wire.model;

/**
 * The domain model reply from a DNS request.
 */
public class DnsReply extends DnsMessage {

	public static DnsReply fromAnswer(DnsSection answer) {
		var res = new DnsReply();
		res.setAnswer(answer);
		return res;
	}

	@Override
	public String toString() {
		return "DnsReply [getQuestion()=" + getQuestion() + ", getAnswer()=" + getAnswer() + ", getAuthority()="
				+ getAuthority() + "]";
	}
}
