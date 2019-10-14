package dk.mada.dns.filter;

import java.util.function.Predicate;

/**
 * Block list is externally sources list of no-go domains.
 */
public interface Blockedlist extends Predicate<String> {
	
}
