package dk.mada.dns.filter;

import java.util.function.Predicate;

/**
 * White list is locally defined list of domains to accept,
 * regardless of block list status for the fomains.
 */
public interface Whitelist extends Predicate<String> {
	
}
