package dk.mada.dns.rest;

import static java.util.stream.Collectors.toList;

import dk.mada.dns.config.Configuration;
import dk.mada.dns.config.Domain;
import dk.mada.dns.config.Host;
import dk.mada.dns.rest.dto.DomainDto;
import dk.mada.dns.rest.dto.FilterDto;
import dk.mada.dns.rest.dto.HostDto;

public class DomainToDtoMapper {
	public static DomainDto domainDto(Domain d) {
		var dto = new DomainDto();
		dto.domain = d.getName();
		dto.reason = d.getReason();
		return dto;
	}
	
	public static HostDto hostDto(Host h) {
		var dto = new HostDto();
		dto.host = h.getName();
		dto.reason = h.getReason();
		return dto;
	}
	
	public static FilterDto filterDto(Configuration c) {
		var dto = new FilterDto();
		dto.blacklistedDomains = c.getBlacklistedDomains().stream()
				.map(d -> domainDto(d))
				.collect(toList());
		dto.blacklistedHosts = c.getBlacklistedHosts().stream()
				.map(h -> hostDto(h))
				.collect(toList());
		dto.whitelistedDomains = c.getWhitelistedDomains().stream()
				.map(d -> domainDto(d))
				.collect(toList());
		dto.whitelistedHosts = c.getWhitelistedHosts().stream()
				.map(h -> hostDto(h))
				.collect(toList());
		return dto;
	}
}
