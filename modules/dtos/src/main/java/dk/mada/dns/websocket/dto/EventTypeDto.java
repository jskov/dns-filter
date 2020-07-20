package dk.mada.dns.websocket.dto;

public enum EventTypeDto {
	QUERY,
	ALLOWED,
	DENIED,
	BLOCKED,
	PASSTHROUGH, // 
	FAILED,
	BYPASS,		// Used when bypassing filtering
	TOGGLE;		// Used to pass commands to lookup engine
}
