package dk.mada.dns.filter;

import java.util.function.Predicate;

/**
 * Allow is locally defined list of domains to accept,
 * regardless of external block list status for the domains.
 */
public interface Allow extends Predicate<String> {
	
}
