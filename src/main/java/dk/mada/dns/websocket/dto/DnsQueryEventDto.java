package dk.mada.dns.websocket.dto;

public class DnsQueryEventDto {
	public String hostname;
	public String reply;
	@Override
	public String toString() {
		return "DnsQueryEventDto [hostname=" + hostname + ", reply=" + reply + "]";
	}
}
