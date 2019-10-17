package dk.mada.dns.lookup;

public enum LookupState {
	QUERY,
	WHITELISTED,
	BLACKLISTED,
	BLOCKED,
	PASSTHROUGH, // 
	FAILED,
	TOGGLE		// Used to pass commands to lookup engine
}
