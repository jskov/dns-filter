package dk.mada.dns.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence of configuration.
 * 
 * Blacklisted entries stored as:
 * -hostname : reason
 * -.domain  : reason
 * 
 * Whitelisted entries stored as:
 * +hostname : reason
 * +.domain  : reason
 * 
 * Preferences stored as:
 * :key=value
 */
@Dependent
public class ConfigurationSerializer {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationSerializer.class);

	private static final String BLOCKED_TTL_SECONDS = "blockedTtlSeconds";
	/** Sensible default minimal length of an entry.
	* -x.ru
	* :key=1
	*/
	private static final int MINIMUM_VALID_ENTRY_LENGTH = 5;
	private static final String NL = System.lineSeparator();
	
	private static final String EXTERNAL_PROVIDED_DIR = System.getenv("DNS_FILTER_CONFIG_DIR");
	private static final Path STORAGE_DIR = Paths.get(EXTERNAL_PROVIDED_DIR == null ? "/opt/data/dns-filter" : EXTERNAL_PROVIDED_DIR);
	private static final Path STORAGE = STORAGE_DIR.resolve("config.txt");
	
	private static final Pattern DOMAIN_PATTERN = Pattern.compile("([+-])[.]([^:]+)(?::(.*))?");
	private static final Pattern HOST_PATTERN =   Pattern.compile("([+-])([^.][^:]+)(?::(.*))?");
	private static final Pattern PREF_PATTERN = Pattern.compile("^:([^=]+)=(.*)");
	
	ConfigurationModel reload() {
		ConfigurationModel model = new ConfigurationModel();
		if (Files.exists(STORAGE)) {
			try (Stream<String> lines = Files.lines(STORAGE, StandardCharsets.UTF_8)) {
				load(model, lines);
			} catch (IOException e) {
				logger.warn("Failed to read configuration from " + STORAGE, e);
			}
		} else {
			logger.warn("No configuration found at {}", STORAGE);
		}
		return model;
	}
	
	boolean store(ConfigurationModel model) {
		try {
			makeBackupCopyIfRequired(model);
			
			try (var w = Files.newBufferedWriter(STORAGE, StandardCharsets.UTF_8)) {
				w.write(modelToStr(model));
			}
			
			return true;
		} catch (IOException e) {
			logger.warn("Failed to persist configuration", e);
			return false;
		}
	}
	
	public void load(ConfigurationModel model, Stream<String> lines) {
		lines.filter(l -> !l.startsWith("#"))
		 .map(String::trim)
		 .filter(l -> l.length() >= MINIMUM_VALID_ENTRY_LENGTH)
		 .forEach(l -> loadLine(model, l));
	}

	private void loadLine(ConfigurationModel model, String l) {
		logger.debug("Load line '{}'", l);
		var m = PREF_PATTERN.matcher(l);
		if (m.matches()) {
			loadPropertyLine(model, m);
			return;
		}
		
		m = HOST_PATTERN.matcher(l);
		if (m.matches()) {
			loadHostLine(model, m);
			return;
		}

		m = DOMAIN_PATTERN.matcher(l);
		if (m.matches()) {
			loadDomainLine(model, m);
			return;
		}
	}
	
	private void loadDomainLine(ConfigurationModel model, Matcher m) {
		boolean blacklist = m.group(1).equals("-");
		String domain = m.group(2).trim();
		String reason = m.group(3);
		reason = reason == null ? "" : reason.trim();

		logger.debug("Load domain line {} {}, because: {}", blacklist ? "blacklist" : "whitelist", domain, reason);
		
		if (blacklist) {
			model.blacklistDomain(domain, reason);
		} else {
			model.whitelistDomain(domain, reason);
		}
	}

	private void loadHostLine(ConfigurationModel model, Matcher m) {
		boolean blacklist = m.group(1).equals("-");
		String host = m.group(2).trim();
		String reason = m.group(3);
		reason = reason == null ? "" : reason.trim();

		logger.debug("Load host line {} {}, because: {}", blacklist ? "blacklist" : "whitelist", host, reason);
		
		if (blacklist) {
			model.blacklistHost(host, reason);
		} else {
			model.whitelistHost(host, reason);
		}
	}

	private void loadPropertyLine(ConfigurationModel model, Matcher m) {
		String key = m.group(1).trim();
		String value = m.group(2).trim();
		
		logger.debug("Load property line '{}' = '{}'", key, value);
		try {
			if (BLOCKED_TTL_SECONDS.equals(key)) {
				model.setBlockedTtlSeconds(Integer.parseInt(value));
			}
		} catch (Exception e) {
			logger.warn("Failed to load key/value {}:{}", key, value, e);
		}
	}

	private String modelToStr(ConfigurationModel model) {
		StringBuilder sb = new StringBuilder();

		sb.append(model.getBlacklistedDomains().stream()
					   .map(d -> "-." + d.getDomain() + ":" + d.getReason())
					   .collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(model.getBlacklistedHosts().stream()
				   .map(h -> "-" + h.getHost() + ":" + h.getReason())
				   .collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(model.getWhitelistedDomains().stream()
				   .map(d -> "+." + d.getDomain() + ":" + d.getReason())
				   .collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(model.getWhitelistedHosts().stream()
				.map(h -> "+" + h.getHost() + ":" + h.getReason())
				.collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(":" + BLOCKED_TTL_SECONDS + "=" + model.getBlockedTtlSeconds()).append(NL);
		
		return sb.toString();		
	}
	
	private void makeBackupCopyIfRequired(ConfigurationModel model) throws IOException {
		Path backupDir = STORAGE_DIR.resolve("backup");
		Files.createDirectories(backupDir);

		var todaysBackupSuffix = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		var name = "config_" + todaysBackupSuffix + ".txt";
		Path backupFile = backupDir.resolve(name);
		
		if (Files.exists(backupFile) || !Files.exists(STORAGE)) {
			return;
		}
		
		Files.copy(STORAGE, backupFile);
	}
}
