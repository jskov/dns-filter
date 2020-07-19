package dk.mada.dns.resolver;

import java.util.Optional;

import dk.mada.dns.wire.model.DnsReply;
import dk.mada.dns.wire.model.DnsRequest;

/**
 * Resolves a DNS request. Entirely model based.
 */
public interface Resolver {
	/**
	 * Resolve a DNS request.
	 * 
	 * Will first translate to whichever wire form is used (presently only
	 * UDP), send to an upstream server, and translate back to a model reply.
	 * 
	 * @param clientIp IP address of client.
	 * @param request DNS model request.
	 * @return DNS model reply.
	 */
	Optional<DnsReply> resolve(String clientIp, DnsRequest request);
}