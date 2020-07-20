package dk.mada.dns.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	@Inject private ConfigurationSerializer serializer;
	
	private ConfigurationModel model;
	private List<ConfigurationChangeListener> listeners = new ArrayList<>();
	
	public void loadConfiguration() {
		model = serializer.load();
		
		logger.info("Loaded configuration\n{}", getSummary());
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
    
	public void denyDomain(String domain, String reason) {
		model.denyDomain(domain, reason);
		update();
	}
	public void denyHost(String hostname, String reason) {
		model.denyHost(hostname, reason);
		update();
	}

	public void allowDomain(String domain, String reason) {
		model.allowDomain(domain, reason);
		update();
	}
	public void allowHost(String hostname, String reason) {
		model.allowHost(hostname, reason);
		update();
	}

	public Set<String> getDeniedHostNames() {
		return model.getDeniedHostNames();
	}
	public Set<String> getDeniedDomainNames() {
		return model.getDeniedDomainNames();
	}
	public Set<String> getAllowedHostNames() {
		return model.getAllowedHostNames();
	}
	public Set<String> getAllowedDomainNames() {
		return model.getAllowedDomainNames();
	}
	
	public Collection<Domain> getDeniedDomains() {
		return model.getDeniedDomains();
	}

	public Collection<Host> getDeniedHosts() {
		return model.getDeniedHosts();
	}

	public Collection<Domain> getAllowedDomains() {
		return model.getAllowedDomains();
	}

	public Collection<Host> getAllowedHosts() {
		return model.getAllowedHosts();
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
				.append(" denied hosts:\n  ")
				.append(String.join("\n  ", model.getDeniedHostNames()))
				.append("\n\n denied domains:\n  ")
				.append(String.join("\n  ", model.getDeniedDomainNames()))
				.append("\n\n allowed hosts:\n  ")
				.append(String.join("\n  ", model.getAllowedHostNames()))
				.append("\n\n allowed domains:\n  ")
				.append(String.join("\n  ", model.getAllowedDomainNames()))
				.append("\n")
				.toString();
	}
}
