package dk.mada.dns.lookup;

import dk.mada.dns.wire.model.DnsRequest;

public class Query {
	private final String clientIp;
	private LookupState state;

	public Query(String clientIp, String hostQuery) {
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
		return null;
	}

	public void setState(LookupState state) {
		this.state = state;
	}
}
