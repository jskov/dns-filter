package dk.mada.dns.wire.model;

import java.util.Objects;

//https://www.ietf.org/rfc/rfc1035.txt
	
/**
 * DNS message - containing both question and answers.
 */
abstract class DnsMessage {
	private final DnsSectionQuestion questionSection;
	private DnsSectionAnswer answer;
	private DnsSectionAuthority authority;
	private DnsSectionAdditional additional;

	protected DnsMessage(DnsSectionQuestion questionSection) {
		this.questionSection = Objects.requireNonNull(questionSection);
	}
	
	public abstract DnsHeader getHeader();

	public DnsSectionQuestion getQuestionSection() {
		return questionSection;
	}
	
	public DnsRecordQ getQuestion() {
		return questionSection
				.getRecords()
				.get(0)
				.asRecordQ()
				.orElseThrow(() -> new IllegalStateException("Message contains no question?!"));
	}
	
	public boolean containsUnhandledRequestRecords() {
		return questionSection.containsUnhandledRecords();

	}

	public boolean containsUnhandledReplyRecords() {
		return answer.containsUnhandledRecords()
				|| authority.containsUnhandledRecords()
				|| additional.containsUnhandledRecords();
	}

	public DnsSectionAnswer getAnswer() {
		return answer;
	}
	public void setAnswer(DnsSectionAnswer answer) {
		this.answer = answer;
	}
	public DnsSectionAuthority getAuthority() {
		return authority;
	}
	public void setAuthority(DnsSectionAuthority authority) {
		this.authority = authority;
	}
	public DnsSectionAdditional getAdditional() {
		return additional;
	}
	public void setAdditional(DnsSectionAdditional additional) {
		this.additional = additional;
	}
}
