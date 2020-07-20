package dk.mada.dns.filter;

import java.util.function.Predicate;

/**
 * Block is externally sources list of no-go domains.
 */
public interface Block extends Predicate<String> {
	
}
