package dk.mada.dns.lookup;

public enum LookupState {
	QUERY,
	ALLOWED,
	DENIED,
	BLOCKED,
	PASSTHROUGH, // 
	FAILED,
	BYPASS,		// Used when bypassing filtering
	TOGGLE		// Used to pass commands to lookup engine
}
