package dk.mada.dns.filter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.mada.dns.config.ConfigurationChangeListener;
import dk.mada.dns.config.Configuration;

/**
 * The whitelist as configured by the user.
 */
@ApplicationScoped
public class ConfiguredWhitelist extends HostDomainNameMatcher implements ConfigurationChangeListener, Whitelist {
	@Inject private Configuration configuration;
	
	public ConfiguredWhitelist() {
		super("Configured whitelist");
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
		setHosts(configuration.getWhitelistedHostNames());
		setDomains(configuration.getWhitelistedDomainNames());
	}
}
