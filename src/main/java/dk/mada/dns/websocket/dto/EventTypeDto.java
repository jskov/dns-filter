package dk.mada.dns.websocket.dto;

import dk.mada.dns.lookup.LookupState;

public enum EventTypeDto {
	QUERY,
	WHITELISTED,
	BLACKLISTED,
	BLOCKED,
	PASSTHROUGH, // 
	FAILED,
	BYPASS,		// Used when bypassing filtering
	TOGGLE;		// Used to pass commands to lookup engine
	
	public static EventTypeDto from(LookupState state) {
		return EventTypeDto.valueOf(state.name());
	}
}
