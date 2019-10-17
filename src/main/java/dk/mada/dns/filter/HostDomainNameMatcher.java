package dk.mada.dns.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matcher looking for a name matching either
 * listed host names or (partial) domain names.
 */
public class HostDomainNameMatcher {
	private static final Logger logger = LoggerFactory.getLogger(HostDomainNameMatcher.class);
	
	private final String matcherName;
	private final Set<String> hosts;
	private final Set<String> domains;
	
	public HostDomainNameMatcher(String name, Collection<String> hosts, Collection<String> domains) {
		this.matcherName = name;
		this.hosts = new HashSet<>(hosts);
		this.domains = new HashSet<>(domains);
	}
	
	public boolean test(String domainName) {
		logger.debug("{} processing {}", matcherName, domainName);
		if (hosts.contains(domainName)) {
			logger.debug(" matches host name {}", domainName);
			return true;
		}
		
		List<String> parts = Arrays.asList(domainName.split("\\."));
		if (parts.size() == 1) {
			logger.debug(" no match");
			return false;
		}
		
		Collections.reverse(parts);
		Iterator<String> ix = parts.iterator();
		String name = ix.next();
		for (; ix.hasNext();) {
			name = ix.next() + "." + name;
			logger.debug(" looking for domain matching {}", name);
			
			if (domains.contains(name)) {
				logger.debug(" matching domain name {} due to blocked parent {}", domainName, name);
				return true;
			}
		}
		
		logger.debug(" no domain match");
		
		return false;
	}
	
	public synchronized List<String> getBlockedHostNames() {
		return hosts.stream()
				.sorted()
				.collect(Collectors.toList());
	}

	public synchronized List<String> getBlockedDomains() {
		return domains.stream()
				.sorted()
				.collect(Collectors.toList());
	}
}
