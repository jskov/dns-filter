package dk.mada.dns.lookup;

import dk.mada.dns.wire.model.DnsRequest;

/**
 * Represents a DNS query from a client.
 */
public class Query {
	private final String clientIp;
	private DnsRequest request;

	public Query(DnsRequest request, String clientIp) {
		this.request = request;
		this.clientIp = clientIp;
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
}
