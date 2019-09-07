package dk.mada.dns.wire.model;

public class DnsRequest extends DnsMessage {

	private static final String NOT_PERTINENT_IN_A_REQUEST = "Not pertinent in a request";

	@Override
	public DnsSection getAnswer() {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public void setAnswer(DnsSection answer) {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public DnsSection getAuthority() {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public void setAuthority(DnsSection authority) {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public DnsSection getAdditional() {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public void setAdditional(DnsSection additional) {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public String toString() {
		return "DnsRequest [getQuestion()=" + getQuestion() + "]";
	}
}
