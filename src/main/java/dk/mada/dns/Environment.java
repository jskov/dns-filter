package dk.mada.dns;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;


/**
 * Environment-based configuration of the application.
 */
@ApplicationScoped
public class Environment {
	private static final Path DATA_DIR_DEFAULT = Paths.get("/opt/data/dns-filter");
	private static final String CONFIG_DIR_ENV = System.getenv("DNS_FILTER_CONFIG_DIR");
	private static final Path CONFIG_DIR = CONFIG_DIR_ENV == null ? DATA_DIR_DEFAULT : Paths.get(CONFIG_DIR_ENV);

	private static final boolean IS_GRADLE_TEST_ENV = Boolean.parseBoolean(System.getenv("GRADLE_TEST"));
	private static final Path CACHE_DIR_TEST = Paths.get(System.getProperty("java.io.tmpdir")).resolve("_dns-filter");
	private static final Path CACHE_DIR = IS_GRADLE_TEST_ENV ? CACHE_DIR_TEST : DATA_DIR_DEFAULT;

	
	public Path getConfigDir() {
		return CONFIG_DIR;
	}
	
	public Path getCacheDir() {
		return CACHE_DIR;
	}
}
