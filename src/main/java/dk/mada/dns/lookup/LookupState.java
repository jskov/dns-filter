package dk.mada.dns.lookup;

public enum LookupState {
	QUERY,
	WHITELISTED,
	BLACKLISTED,
	BLOCKED,
	PASSTHROUGH, // 
	FAILED,
	BYPASS,		// Used when bypassing filtering
	TOGGLE		// Used to pass commands to lookup engine
}
