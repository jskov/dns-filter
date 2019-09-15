package dk.mada.dns.filter;

import java.util.function.Predicate;

public interface Blacklist extends Predicate<String> {
	
}
