package dk.mada.dns.filter.blocker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.Blockedlist;

/**
 * Fetches host and domain names from https://github.com/notracking/hosts-blocklists
 */
public class FetchLists {
	private static final Logger logger = LoggerFactory.getLogger(FetchLists.class);
	private static final String ADDRESS_SUFFIX = "/::";
	private static final String ADDRESS_PREFIX = "address=/";
	private static final String IP_PREFIX = "0.0.0.0 ";
	
	private static Predicate<String> VALID_DOMAIN_PATTERN = Pattern.compile("[a-z0-9.-_]+").asPredicate();
	
	public Blockedlist fetch() {
		Set<String> hostNames = readUrl(getHostsUrl(), this::filterHostNames);
		Set<String> domainNames = readUrl(getDomainsUrl(), this::filterDomainNames);

		logger.info("Got {} host names and {} domain names", hostNames.size(), domainNames.size());

		return new UpstreamBlocklist(hostNames, domainNames);
	}
	
	private Set<String> filterHostNames(Stream<String> lines) {
		return lines.filter(l -> l.startsWith(IP_PREFIX))
				.map(l -> l.substring(IP_PREFIX.length()))
				.filter(FetchLists.VALID_DOMAIN_PATTERN)
				.collect(Collectors.toSet());
	}

	private Set<String> filterDomainNames(Stream<String> lines) {
		return lines
				.filter(l -> l.endsWith(ADDRESS_SUFFIX))
				.filter(l -> l.startsWith(ADDRESS_PREFIX))
				.map(l -> l.substring(ADDRESS_PREFIX.length(), l.length()-ADDRESS_SUFFIX.length()))
				.filter(FetchLists.VALID_DOMAIN_PATTERN)
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
