package dk.mada.dns.filter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.mada.dns.config.Configuration;
import dk.mada.dns.config.ConfigurationChangeListener;

/**
 * The blacklist as configured by the user.
 */
@ApplicationScoped
public class ConfiguredBlacklist extends HostDomainNameMatcher implements ConfigurationChangeListener, Blacklist {
	@Inject private Configuration configuration;
	
	public ConfiguredBlacklist() {
		super("Configured blacklist");
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
		setHosts(configuration.getBlacklistedHostNames());
		setDomains(configuration.getBlacklistedDomainNames());
	}
}
