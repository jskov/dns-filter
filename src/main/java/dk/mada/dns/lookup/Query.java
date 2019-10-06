package dk.mada.dns.lookup;

import dk.mada.dns.wire.model.DnsRequest;

/**
 * Represents a DNS query on its way through the lookup state machine.
 */
public class Query {
	private final String clientIp;
	private LookupState state;
	private DnsRequest request;

	public Query(DnsRequest request, String clientIp, String hostQuery) {
		this.request = request;
		this.clientIp = clientIp;
		state = LookupState.QUERY;
	}
	
	public String getRequestName() {
		return getRequest().getQuestion().getName().getName();
	}
	
	public String getClientIp() {
		return clientIp;
	}

	public DnsRequest getRequest() {
		return request;
	}

	public void setState(LookupState state) {
		this.state = state;
	}

	public LookupState getState() {
		return state;
	}
}
