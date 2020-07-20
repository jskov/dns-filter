package dk.mada.dns.filter;

import java.util.function.Predicate;

/**
 * Deny is our locally defined list of no-go
 * domains.
 */
public interface Deny extends Predicate<String> {
	
}
