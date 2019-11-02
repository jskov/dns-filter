package dk.mada.dns.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Controller providing access to persisted configuration.
 * 
 * All changes cause persistence. A backup is made (once
 * per day) before changes are persisted.
 * 
 * Listeners can register for notifications. Note that at
 * present this is abused somewhat; should probably be on
 * each of the model lists.
 */
@ApplicationScoped
public class Configuration {
	@Inject private ConfigurationSerializer serializer;
	private ConfigurationModel model;
	private List<ConfigurationChangeListener> listeners = new ArrayList<>();
	
	@PostConstruct
	public void loadPrefsAtStartup() {
		model = serializer.reload();
	}

    private void update() {
    	serializer.store(model);
    	listeners.forEach(l -> l.configurationChanged());
    }

    public void addChangeListener(ConfigurationChangeListener l) {
    	listeners.add(l);
    }

    public void removePropertyChangeListener(ConfigurationChangeListener l) {
    	listeners.remove(l);
    }
    
	public void blacklistDomain(String domain, String reason) {
		model.blacklistDomain(domain, reason);
		update();
	}
	public void blacklistHost(String hostname, String reason) {
		model.blacklistHost(hostname, reason);
		update();
	}
	public void unblacklistDomain(String domain) {
		model.unblacklistDomain(domain);
		update();
	}
	public void unblacklistHost(String hostname) {
		model.unblacklistHost(hostname);
		update();
	}

	public void whitelistDomain(String domain, String reason) {
		model.whitelistDomain(domain, reason);
		update();
	}
	public void whitelistHost(String hostname, String reason) {
		model.whitelistHost(hostname, reason);
		update();
	}
	public void unwhitelistDomain(String domain) {
		model.unwhitelistDomain(domain);
		update();
	}
	public void unwhitelistHost(String hostname) {
		model.unwhitelistHost(hostname);
		update();
	}

	public Set<String> getBlacklistedHostNames() {
		return model.getBlacklistedHostNames();
	}
	public Set<String> getBlacklistedDomainNames() {
		return model.getBlacklistedDomainNames();
	}
	public Set<String> getWhitelistedHostNames() {
		return model.getWhitelistedHostNames();
	}
	public Set<String> getWhitelistedDomainNames() {
		return model.getWhitelistedDomainNames();
	}
	
	public Collection<Domain> getBlacklistedDomains() {
		return model.getBlacklistedDomains();
	}

	public Collection<Host> getBlacklistedHosts() {
		return model.getBlacklistedHosts();
	}

	public Collection<Domain> getWhitelistedDomains() {
		return model.getWhitelistedDomains();
	}

	public Collection<Host> getWhitelistedHosts() {
		return model.getWhitelistedHosts();
	}
	
	public int getBlockedTtlSeconds() {
		return model.getBlockedTtlSeconds();
	}
	public void setBlockedTtlSeconds(int blockedTtlSeconds) {
		model.setBlockedTtlSeconds(blockedTtlSeconds);
		serializer.store(model);
	}

	public String getSummary() {
		return new StringBuilder("Configuration summary:\n")
				.append(" blacklisted hosts:\n  ")
				.append(String.join("\n  ", model.getBlacklistedHostNames()))
				.append("\n\n blacklisted domains:\n  ")
				.append(String.join("\n  ", model.getBlacklistedDomainNames()))
				.append("\n\n whitelisted hosts:\n  ")
				.append(String.join("\n  ", model.getWhitelistedHostNames()))
				.append("\n\n whitelisted domains:\n  ")
				.append(String.join("\n  ", model.getWhitelistedDomainNames()))
				.append("\n")
				.toString();
	}
}
