package dk.mada.dns.filter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.mada.dns.config.Configuration;
import dk.mada.dns.config.ConfigurationChangeListener;

/**
 * The denied hosts/domains as configured by the user.
 */
@ApplicationScoped
public class ConfiguredDenied extends HostDomainNameMatcher implements ConfigurationChangeListener, Deny {
	@Inject Configuration configuration;
	
	public ConfiguredDenied() {
		super("Configured denies");
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
		setHosts(configuration.getDeniedHostNames());
		setDomains(configuration.getDeniedDomainNames());
	}
}
