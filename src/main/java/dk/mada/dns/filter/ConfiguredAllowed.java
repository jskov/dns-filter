package dk.mada.dns.filter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.mada.dns.config.Configuration;
import dk.mada.dns.config.ConfigurationChangeListener;

/**
 * The allowed hosts/domains as configured by the user.
 */
@ApplicationScoped
public class ConfiguredAllowed extends HostDomainNameMatcher implements ConfigurationChangeListener, Allow {
	@Inject Configuration configuration;
	
	public ConfiguredAllowed() {
		super("Configured allows");
	}
	
	@PostConstruct
	public void addListener() {
		configuration.addChangeListener(this);
	}

	@Override
	public boolean test(String domainName) {
		return super.test(domainName);
	}
	
	@Override
	public void configurationChanged() {
		setHosts(configuration.getAllowedHostNames());
		setDomains(configuration.getAllowedDomainNames());
	}
}
