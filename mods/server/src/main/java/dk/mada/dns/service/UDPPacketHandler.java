package dk.mada.dns.service;

import java.nio.ByteBuffer;

public interface UDPPacketHandler {
	/**
	 * Process incoming UDP packet, return a reply.
	 * @param clientIp Client IP 
	 * @param request Request from client.
	 * @return Reply to be sent back to client.
	 */
	ByteBuffer process(String clientIp, ByteBuffer request);
}
