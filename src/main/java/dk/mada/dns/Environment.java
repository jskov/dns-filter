package dk.mada.dns;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;


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

	public static final int LISTEN_PORT_DNS_DEFAULT = 8053;
	private static final String LISTEN_PORT_DNS_ENV = System.getenv("DNS_FILTER_PORT_DNS");
	private static final int LISTEN_PORT_DNS = LISTEN_PORT_DNS_ENV == null ? LISTEN_PORT_DNS_DEFAULT : Integer.parseInt(LISTEN_PORT_DNS_ENV);

	private static final int RUN_AS_USER_DEFAULT = 65534; // nobody on fedora 31
	private static final String RUN_AS_USERID_ENV = System.getenv("DNS_FILTER_RUN_AS");
	private static final int RUN_AS_USERID = RUN_AS_USERID_ENV == null ? RUN_AS_USER_DEFAULT : Integer.parseInt(RUN_AS_USERID_ENV);

	@ConfigProperty(name = "dns-filter.version")
	private String version;

	@ConfigProperty(name = "dns-filter.revision")
	private String revision;

	public String getVersion() {
		return version;
	}

	public String getRevision() {
		return revision;
	}

	public Path getConfigDir() {
		return CONFIG_DIR;
	}
	
	public Path getCacheDir() {
		return CACHE_DIR;
	}
	
	public int getListenPortDns() {
		return LISTEN_PORT_DNS;
	}
	
	public int getRunAsUserId() {
		return RUN_AS_USERID;
	}
}
