package dk.mada.dns.websocket.dto;

/**
 * Describes a DNS lookup event for listeners.
 */
public class DnsQueryEventDto {
	public EventTypeDto type;
	public String hostname;
	public String clientIp;
	public long ttl;
	public String ip;
	@Override
	public String toString() {
		return "DnsQueryEventDto [type=" + type + ", hostname=" + hostname + ", ttl=" + ttl + ", ip=" + ip + "]";
	}
}
