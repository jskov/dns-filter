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
	private Map<String, Domain> blacklistedDomains = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Domain> whitelistedDomains = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Host> blacklistedHosts = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Host> whitelistedHosts = Collections.synchronizedMap(new HashMap<>());
	
	private Set<String> blacklistedDomainNamesReadOnly = Collections.unmodifiableSet(blacklistedDomains.keySet());
	private Set<String> whitelistedDomainNamesReadOnly = Collections.unmodifiableSet(whitelistedDomains.keySet());
	private Set<String> blacklistedHostNamesReadOnly = Collections.unmodifiableSet(blacklistedHosts.keySet());
	private Set<String> whitelistedHostNamesReadOnly = Collections.unmodifiableSet(whitelistedHosts.keySet());
	
	private Collection<Domain> blacklistedDomainsReadOnly = Collections.unmodifiableCollection(blacklistedDomains.values());
	private Collection<Domain> whitelistedDomainsReadOnly = Collections.unmodifiableCollection(whitelistedDomains.values());
	private Collection<Host> blacklistedHostsReadOnly = Collections.unmodifiableCollection(blacklistedHosts.values());
	private Collection<Host> whitelistedHostsReadOnly = Collections.unmodifiableCollection(whitelistedHosts.values());
	
	private int blockedTtlSeconds = (int)Duration.ofMinutes(5).toSeconds();
	
	public void blacklistDomain(String domain, String reason) {
		blacklistedDomains.put(domain, new Domain(domain, reason));
	}
	public void unblacklistDomain(String domain) {
		blacklistedDomains.remove(domain);
	}
	
	public void blacklistHost(String host, String reason) {
		blacklistedHosts.put(host, new Host(host, reason));
	}
	public void unblacklistHost(String host) {
		blacklistedHosts.remove(host);
	}

	public void whitelistDomain(String domain, String reason) {
		whitelistedDomains.put(domain, new Domain(domain, reason));
	}
	public void unwhitelistDomain(String domain) {
		whitelistedDomains.remove(domain);
	}

	public void whitelistHost(String host, String reason) {
		whitelistedHosts.put(host, new Host(host, reason));
	}
	public void unwhitelistHost(String host) {
		whitelistedHosts.remove(host);
	}

	
	public Collection<Host> getBlacklistedHosts() {
		return blacklistedHostsReadOnly;
	}

	public Collection<Host> getWhitelistedHosts() {
		return whitelistedHostsReadOnly;
	}

	public Collection<Domain> getBlacklistedDomains() {
		return blacklistedDomainsReadOnly;
	}

	public Collection<Domain> getWhitelistedDomains() {
		return whitelistedDomainsReadOnly;
	}

	public Set<String> getBlacklistedHostNames() {
		return blacklistedHostNamesReadOnly;
	}
	public Set<String> getBlacklistedDomainNames() {
		return blacklistedDomainNamesReadOnly;
	}
	public Set<String> getWhitelistedHostNames() {
		return whitelistedHostNamesReadOnly;
	}
	public Set<String> getWhitelistedDomainNames() {
		return whitelistedDomainNamesReadOnly;
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
