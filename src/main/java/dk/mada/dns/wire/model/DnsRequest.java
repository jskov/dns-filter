package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

public class DnsRequest extends DnsMessage {
	private static final String NOT_PERTINENT_IN_A_REQUEST = "Not pertinent in a request";
	private final ByteBuffer baseWireRequest;
	private final DnsHeaderQuery header;
	
	public DnsRequest(DnsHeaderQuery header, DnsSectionQuestion question, DnsSectionAdditional additional, ByteBuffer baseWireRequest) {
		super(question);
		setAdditional(additional);
		this.header = header;
		this.baseWireRequest = baseWireRequest.asReadOnlyBuffer();
	}

	public String getQueryName() {
		return getQuestion().getName().getName();
	}
	
	@Override
	public DnsHeaderQuery getHeader() {
		return header;
	}

	public ByteBuffer asWirePacket() {
		return baseWireRequest.rewind();
	}

	@Override
	public DnsSectionAnswer getAnswer() {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public void setAnswer(DnsSectionAnswer answer) {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public DnsSectionAuthority getAuthority() {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}
	@Override
	public void setAuthority(DnsSectionAuthority authority) {
		throw new IllegalStateException(NOT_PERTINENT_IN_A_REQUEST);
	}

	@Override
	public String toString() {
		return "DnsRequest [header=" + header + ", getQuestion()=" + getQuestion() + ", getAdditional()="
				+ getAdditional() + "]";
	}
}
