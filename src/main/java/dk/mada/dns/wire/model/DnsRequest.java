package dk.mada.dns.wire.model;

import java.nio.ByteBuffer;

public class DnsRequest extends DnsMessage {
	private static final String NOT_PERTINENT_IN_A_REQUEST = "Not pertinent in a request";
	private final ByteBuffer baseWireRequest;
	
	private DnsRequest(DnsSection question, ByteBuffer baseWireRequest) {
		super(question);
		this.baseWireRequest = baseWireRequest.asReadOnlyBuffer();
	}

	public static DnsRequest fromWireRequest(DnsSection question, ByteBuffer wireRequest) {
		return new DnsRequest(question, wireRequest);
	}
	
	public ByteBuffer asWirePacket() {
		return baseWireRequest.rewind();
	}

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
