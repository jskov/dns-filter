package dk.mada.dns.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.Environment;

/**
 * Persistence of configuration.
 * 
 * Denied entries stored as:
 * -hostname : reason
 * -.domain  : reason
 * 
 * Allowed entries stored as:
 * +hostname : reason
 * +.domain  : reason
 * 
 * Preferences stored as:
 * :key=value
 */
@Dependent
public class ConfigurationSerializer {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationSerializer.class);

	@Inject Environment environment;
	
	private static final String BLOCKED_TTL_SECONDS = "blockedTtlSeconds";
	/** Sensible default minimal length of an entry.
	* -x.ru
	* :key=1
	*/
	private static final int MINIMUM_VALID_ENTRY_LENGTH = 5;
	private static final String NL = System.lineSeparator();
	
	private static final Pattern DOMAIN_PATTERN = Pattern.compile("([+-])[.]([^:]+)(?::(.*))?");
	private static final Pattern HOST_PATTERN =   Pattern.compile("([+-])([^.][^:]+)(?::(.*))?");
	private static final Pattern PREF_PATTERN = Pattern.compile("^:([^=]+)=(.*)");
	
	ConfigurationModel load() {
		Path config = getConfigFile();
		ConfigurationModel model = new ConfigurationModel();
		if (Files.exists(config)) {
			try (Stream<String> lines = Files.lines(config, StandardCharsets.UTF_8)) {
				load(model, lines);
			} catch (IOException e) {
				logger.warn("Failed to read configuration from " + config, e);
			}
		} else {
			logger.warn("No configuration found at {}", config);
		}
		return model;
	}
	
	boolean store(ConfigurationModel model) {
		try {
			makeBackupCopyIfRequired(model);
			
			try (var w = Files.newBufferedWriter(getConfigFile(), StandardCharsets.UTF_8)) {
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
		boolean isDeny = m.group(1).equals("-");
		String domain = m.group(2).trim();
		String reason = m.group(3);
		reason = reason == null ? "" : reason.trim();

		logger.debug("Load domain line {} {}, because: {}", isDeny ? "deny" : "allow", domain, reason);
		
		if (isDeny) {
			model.denyDomain(domain, reason);
		} else {
			model.allowDomain(domain, reason);
		}
	}

	private void loadHostLine(ConfigurationModel model, Matcher m) {
		boolean isDeny = m.group(1).equals("-");
		String host = m.group(2).trim();
		String reason = m.group(3);
		reason = reason == null ? "" : reason.trim();

		logger.debug("Load host line {} {}, because: {}", isDeny ? "deny" : "allow", host, reason);
		
		if (isDeny) {
			model.denyHost(host, reason);
		} else {
			model.allowHost(host, reason);
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

		sb.append(model.getDeniedDomains().stream()
					   .map(d -> "-." + d.getName() + ":" + d.getReason())
					   .collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(model.getDeniedHosts().stream()
				   .map(h -> "-" + h.getName() + ":" + h.getReason())
				   .collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(model.getAllowedDomains().stream()
				   .map(d -> "+." + d.getName() + ":" + d.getReason())
				   .collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(model.getAllowedHosts().stream()
				.map(h -> "+" + h.getName() + ":" + h.getReason())
				.collect(Collectors.joining(NL)));
		sb.append(NL).append(NL);

		sb.append(":" + BLOCKED_TTL_SECONDS + "=" + model.getBlockedTtlSeconds()).append(NL);
		
		return sb.toString();		
	}
	

	private Path getConfigDir() {
		return environment.getConfigDir();
	}

	private Path getConfigFile() {
		return getConfigDir().resolve("config.txt");
	}

	private void makeBackupCopyIfRequired(ConfigurationModel model) throws IOException {
		Path configDir = getConfigDir();
		Path backupDir = configDir.resolve("backup");
		Files.createDirectories(backupDir);

		var todaysBackupSuffix = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		var name = "config_" + todaysBackupSuffix + ".txt";
		Path backupFile = backupDir.resolve(name);
		
		Path config = getConfigFile();
		if (Files.exists(backupFile) || !Files.exists(config)) {
			return;
		}
		
		Files.copy(config, backupFile);
	}
}
