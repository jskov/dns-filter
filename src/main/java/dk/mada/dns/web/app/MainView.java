package dk.mada.dns.web.app;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import dk.mada.dns.config.Configuration;

@Route(value = "config")
public class MainView extends VerticalLayout {
	private static final Logger logger = LoggerFactory.getLogger(MainView.class);
	@Inject	private Configuration config;

	public MainView() {
		add(new H1("Configuration"));
	}
	
	@PostConstruct
	public void after() {
		PrefListComponent wd = new PrefListComponent("Whitelisted domains", config::getWhitelistedDomains, config::unwhitelistDomain, config::whitelistDomain);
		add(wd.getComponent());
		config.addChangeListener(wd::update);
		
		PrefListComponent wh = new PrefListComponent("Whitelisted hosts", config::getWhitelistedHosts, config::unwhitelistHost, config::whitelistHost);
		add(wh.getComponent());
		config.addChangeListener(wh::update);

		PrefListComponent bd = new PrefListComponent("Blacklisted domains", config::getBlacklistedDomains, config::unblacklistDomain, config::blacklistDomain);
		add(bd.getComponent());
		config.addChangeListener(bd::update);
		
		PrefListComponent bh = new PrefListComponent("Blacklisted hosts", config::getBlacklistedHosts, config::unblacklistHost, config::blacklistHost);
		add(bh.getComponent());
		config.addChangeListener(bh::update);

		logger.info("After creation {}", config);
	}
}
