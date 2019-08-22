package dk.mada.dns.websocket.dto;

public enum EventTypeDto {
	FLUSH,
	CACHED,
	WHITE_LISTED,
	BLOCKED,
	PASSTHROUGH,
	FAIL
}
