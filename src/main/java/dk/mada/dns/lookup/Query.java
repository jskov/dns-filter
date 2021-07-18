package dk.mada.dns.lookup;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.wire.model.DnsReplies;
import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;
import dk.mada.dns.wire.model.DnsRequests;

/**
 * Represents a DNS query from a client.
 *
 * Holds the data of a DNS query.
 * 
 * Elements are wire-request, model-request, model-reply, and wire-reply.
 * These are added strictly in that order.
 * 
 * The collection of data makes it easier to log information about
 * failed query processing.
 * 
 */
public class Query {
	private static final Logger logger = LoggerFactory.getLogger(Query.class);
	
	private final String clientIp;
	private final ByteBuffer wireRequest;
	private final DnsRequest request;
	private DnsReply reply;
	private ByteBuffer wireReply;
	private boolean debugBypassRequest;

	private Query(String clientIp, ByteBuffer wireRequest) {
		this.clientIp = clientIp;
		this.wireRequest = wireRequest.asReadOnlyBuffer();

		this.request = DnsRequests.fromWireData(this.wireRequest);
		this.wireRequest.rewind();
	}

	public static Query newFromIp(String clientIp, ByteBuffer wireRequest) {
		return new Query(clientIp, wireRequest);
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

	public ByteBuffer getWireRequest() {
		return wireRequest.asReadOnlyBuffer();
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

	public void setLookupResult(LookupResult lr) {
		reply = lr.getReply();
		if (lr.getState() == LookupState.BYPASS) {
			logger.warn("Bypass lookup");
			wireReply = reply.getOptWireReply();
		} else {
			logger.debug("Decoded reply: {}", reply);
			wireReply = DnsReplies.replyToWireFormat(this);
		}
	}

	public DnsReply getReply() {
		return reply;
	}
	
	public ByteBuffer getWireReply() {
		return wireReply.asReadOnlyBuffer();
	}
}
