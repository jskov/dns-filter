package dk.mada.dns.config;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Model containing configuration.
 */
public class ConfigurationModel {
	private Map<String, Domain> deniedDomains = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Domain> allowedDomains = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Host> deniedHosts = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Host> allowedHosts = Collections.synchronizedMap(new HashMap<>());
	
	private Set<String> deniedDomainNamesReadOnly = Collections.unmodifiableSet(deniedDomains.keySet());
	private Set<String> allowedDomainNamesReadOnly = Collections.unmodifiableSet(allowedDomains.keySet());
	private Set<String> deniedHostNamesReadOnly = Collections.unmodifiableSet(deniedHosts.keySet());
	private Set<String> allowedHostNamesReadOnly = Collections.unmodifiableSet(allowedHosts.keySet());
	
	private Collection<Domain> deniedDomainsReadOnly = Collections.unmodifiableCollection(deniedDomains.values());
	private Collection<Domain> allowedDomainsReadOnly = Collections.unmodifiableCollection(allowedDomains.values());
	private Collection<Host> deniedHostsReadOnly = Collections.unmodifiableCollection(deniedHosts.values());
	private Collection<Host> allowedHostsReadOnly = Collections.unmodifiableCollection(allowedHosts.values());
	
	private int blockedTtlSeconds = (int)Duration.ofMinutes(5).toSeconds();
	
	public void denyDomain(String domain, String reason) {
	    allowedDomains.remove(domain);
		deniedDomains.put(domain, new Domain(domain, reason));
	}
	
	public void denyHost(String host, String reason) {
	    allowedHosts.remove(host);
		deniedHosts.put(host, new Host(host, reason));
	}

	public void allowDomain(String domain, String reason) {
	    deniedDomains.remove(domain);
		allowedDomains.put(domain, new Domain(domain, reason));
	}

	public void allowHost(String host, String reason) {
	    deniedHosts.remove(host);
		allowedHosts.put(host, new Host(host, reason));
	}
	
	public Collection<Host> getDeniedHosts() {
		return deniedHostsReadOnly;
	}

	public Collection<Host> getAllowedHosts() {
		return allowedHostsReadOnly;
	}

	public Collection<Domain> getDeniedDomains() {
		return deniedDomainsReadOnly;
	}

	public Collection<Domain> getAllowedDomains() {
		return allowedDomainsReadOnly;
	}

	public Set<String> getDeniedHostNames() {
		return deniedHostNamesReadOnly;
	}
	public Set<String> getDeniedDomainNames() {
		return deniedDomainNamesReadOnly;
	}
	public Set<String> getAllowedHostNames() {
		return allowedHostNamesReadOnly;
	}
	public Set<String> getWhitelistedDomainNames() {
		return allowedDomainNamesReadOnly;
	}
	
	public int getBlockedTtlSeconds() {
		return blockedTtlSeconds;
	}
	public void setBlockedTtlSeconds(int blockedTtlSeconds) {
		if (blockedTtlSeconds < 5) {
			blockedTtlSeconds = 5;
		}
		this.blockedTtlSeconds = blockedTtlSeconds;
	}
}
