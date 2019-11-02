package dk.mada.dns;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;


/**
 * Environment-based configuration of the application.
 */
@ApplicationScoped
public class Environment {
	private static final String CONFIG_DIR_DEFAULT = "/opt/data/dns-filter";
	private static final String CONFIG_DIR_ENV = System.getenv("DNS_FILTER_CONFIG_DIR");
	private static final Path CONFIG_DIR = Paths.get(CONFIG_DIR_ENV == null ? CONFIG_DIR_DEFAULT : CONFIG_DIR_ENV);

	public Path getConfigDir() {
		return CONFIG_DIR;
	}
	
}
