package dk.mada.dns.filter;

import java.util.function.Predicate;

/**
 * Black list is our locally defined list of no-go
 * domains.
 */
public interface Blacklist extends Predicate<String> {
	
}
