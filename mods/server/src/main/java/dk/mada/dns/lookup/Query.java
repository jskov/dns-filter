package dk.mada.dns.lookup;

import dk.mada.dns.wire.model.DnsRequest;

/**
 * Represents a DNS query from a client.
 */
public class Query {
	private final String clientIp;
	private DnsRequest request;
	private boolean debugBypassRequest;

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

	/**
	 * If set, the request should not be filtered - the nameserver
	 * result should just be returned.
	 * Used for development test data capture.
	 * 
	 * @return true if the wire output should be echoed to the console
	 */
	public boolean isDebugBypassRequest() {
		return debugBypassRequest;
	}

	public void setDebugBypassRequest(boolean debugBypassRequest) {
		this.debugBypassRequest = debugBypassRequest;
	}
}
