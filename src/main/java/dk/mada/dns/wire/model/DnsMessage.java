package dk.mada.dns.wire.model;

//https://www.ietf.org/rfc/rfc1035.txt
	
/**
 * DNS message - containing both question and answers.
 */
class DnsMessage {
	private DnsSection question;
	private DnsSection answer;
	private DnsSection authority;
	private DnsSection additional;
	
	public DnsSection getQuestion() {
		return question;
	}
	public void setQuestion(DnsSection question) {
		this.question = question;
	}
	public DnsSection getAnswer() {
		return answer;
	}
	public void setAnswer(DnsSection answer) {
		this.answer = answer;
	}
	public DnsSection getAuthority() {
		return authority;
	}
	public void setAuthority(DnsSection authority) {
		this.authority = authority;
	}
	public DnsSection getAdditional() {
		return additional;
	}
	public void setAdditional(DnsSection additional) {
		this.additional = additional;
	}
}
