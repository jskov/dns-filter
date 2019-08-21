package dk.mada.dns.websocket.dto;

public class EventDto {
	public String hostname;
	public String reply;
	@Override
	public String toString() {
		return "EventDto [hostname=" + hostname + ", reply=" + reply + "]";
	}
}
