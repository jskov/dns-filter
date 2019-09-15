package dk.mada.dns.lookup;

public enum LookupState {
	QUERY,
	WHITELISTED,
	BLACKLISTED,
	FAILED
}
