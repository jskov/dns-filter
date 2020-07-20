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
		dto.deniedDomains = c.getDeniedDomains().stream()
				.map(d -> domainDto(d))
				.collect(toList());
		dto.deniedHosts = c.getDeniedHosts().stream()
				.map(h -> hostDto(h))
				.collect(toList());
		dto.allowedDomains = c.getAllowedDomains().stream()
				.map(d -> domainDto(d))
				.collect(toList());
		dto.allowedHosts = c.getAllowedHosts().stream()
				.map(h -> hostDto(h))
				.collect(toList());
		return dto;
	}
}
