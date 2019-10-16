package dk.mada.dns.filter.blocker;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Blockedlist;

/**
 * Fetches host and domain names from https://github.com/notracking/hosts-blocklists
 */
@ApplicationScoped
public class BlockedListCacher {
	private static final Logger logger = LoggerFactory.getLogger(BlockedListCacher.class);
	private static final String ADDRESS_SUFFIX = "/::";
	private static final String ADDRESS_PREFIX = "address=/";
	private static final String IP_PREFIX = "0.0.0.0 ";
	
	private static final boolean isGradleTestEnv = Boolean.parseBoolean(System.getenv("GRADLE_TEST"));
	private static final Path TEST_CACHE_DIR = Paths.get(System.getProperty("java.io.tmpdir")).resolve("_dns-filter");
	private static final Path CACHE_DIR = isGradleTestEnv ? TEST_CACHE_DIR : Paths.get("/opt/data/dns-filter");
	private static final Path CACHED_DOMAINNAMES = CACHE_DIR.resolve("_cached_domainnames.txt");
	private static final Path CACHED_HOSTNAMES = CACHE_DIR.resolve("_cached_hostnames.txt");
	
	private static Predicate<String> VALID_DOMAIN_PATTERN = Pattern.compile("[a-z0-9.-_]+").asPredicate();

	private List<String> hostNames = Collections.emptyList();
	private List<String> domainNames = Collections.emptyList();

	public void preloadCache() {
		logger.info("Preloading cache of blocked domain/host names from {}", CACHE_DIR);

		if (Files.notExists(CACHED_HOSTNAMES) || Files.notExists(CACHED_DOMAINNAMES)) {
			refreshCaches();
		}

		try {
			hostNames = Files.readAllLines(CACHED_HOSTNAMES);
		} catch (IOException e) {
			logger.warn("Failed to read cache of blocked host names", e);
		}
		try {
			domainNames = Files.readAllLines(CACHED_DOMAINNAMES);
		} catch (IOException e) {
			logger.warn("Failed to read cache of blocked domain names", e);
		}
	}
	
	public Blockedlist get() {
		logger.info("Provide blocked list of {} host names and {} domain names", hostNames.size(), domainNames.size());
		return new UpstreamBlocklist(hostNames, domainNames);
	}

	private void refreshCaches() {
		logger.info("Refreshing lists of blocked host and domain names");
		List<String> hostNames = readUrl(getHostsUrl(), this::filterHostNames).stream()
				.sorted()
				.collect(toList());
		List<String> domainNames = readUrl(getDomainsUrl(), this::filterDomainNames).stream()
				.sorted()
				.collect(toList());

		if (hostNames.isEmpty() || domainNames.isEmpty()) {
			logger.warn("Upstream provided empty domain/host list, so not updating local cache");
			return;
		}
		
		try {
			Files.createDirectories(CACHE_DIR);
			Files.writeString(CACHED_DOMAINNAMES, String.join("\n", domainNames));
			Files.writeString(CACHED_HOSTNAMES, String.join("\n", hostNames));
		} catch (IOException e) {
			logger.warn("Failed to cache list of blocked host/domain names", e);
		}
	}
	
	private Set<String> filterHostNames(Stream<String> lines) {
		return lines.filter(l -> l.startsWith(IP_PREFIX))
				.map(l -> l.substring(IP_PREFIX.length()))
				.filter(BlockedListCacher.VALID_DOMAIN_PATTERN)
				.collect(Collectors.toSet());
	}

	private Set<String> filterDomainNames(Stream<String> lines) {
		return lines
				.filter(l -> l.endsWith(ADDRESS_SUFFIX))
				.filter(l -> l.startsWith(ADDRESS_PREFIX))
				.map(l -> l.substring(ADDRESS_PREFIX.length(), l.length()-ADDRESS_SUFFIX.length()))
				.filter(BlockedListCacher.VALID_DOMAIN_PATTERN)
				.collect(Collectors.toSet());
	}

	private Set<String> readUrl(URL url, Function<Stream<String>, Set<String>> f) {
		long start = System.currentTimeMillis();
		try (InputStream is = url.openStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr)) {
			Set<String> res = f.apply(br.lines());
			long time = System.currentTimeMillis() - start;
			logger.info("Processed {} in {}ms", url, time);
			return res;
		} catch (IOException e) {
			logger.warn("Failed to fetch list of blocked hosts/domains", e);
		}
		return Collections.emptySet();
	}
	
	private URL getDomainsUrl() {
		try {
			return new URL("https://raw.githubusercontent.com/notracking/hosts-blocklists/master/domains.txt");
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
			
	}

	private URL getHostsUrl() {
		try {
			return new URL("https://raw.githubusercontent.com/notracking/hosts-blocklists/master/hostnames.txt");
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
			
	}
}
