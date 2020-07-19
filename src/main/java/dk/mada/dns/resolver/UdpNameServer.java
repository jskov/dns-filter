package dk.mada.dns.resolver;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Name server lookup based on UDP wire protocol.
 * 
 * At runtime, used for external (upstream) lookups.
 * At testing, used for canned replies so wire encoding/decoding can be tested.
 */
public interface UdpNameServer {
	/**
	 * Makes a UDP based DNS lookup.
	 * 
	 * @param hostname Name of host being looked up - only used for logging output. 
	 * @param query UDP wire based query.
	 * @return UDP wire based reply.
	 */
	Optional<ByteBuffer> lookup(String hostname, ByteBuffer query);
}
