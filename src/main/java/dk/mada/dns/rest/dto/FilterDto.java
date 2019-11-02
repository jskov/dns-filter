package dk.mada.dns.rest.dto;

import static java.util.stream.Collectors.toList;

import java.util.List;

import dk.mada.dns.config.Configuration;

public class FilterDto {
	public List<DomainDto> blacklistedDomains = List.of();
	public List<HostDto> blacklistedHosts = List.of();
	public List<DomainDto> whitelistedDomains = List.of();
	public List<HostDto> whitelistedHosts = List.of();
	
	public static FilterDto from(Configuration c) {
		var dto = new FilterDto();
		dto.blacklistedDomains = c.getBlacklistedDomains().stream()
				.map(DomainDto::from)
				.collect(toList());
		dto.blacklistedHosts = c.getBlacklistedHosts().stream()
				.map(HostDto::from)
				.collect(toList());
		dto.whitelistedDomains = c.getWhitelistedDomains().stream()
				.map(DomainDto::from)
				.collect(toList());
		dto.whitelistedHosts = c.getWhitelistedHosts().stream()
				.map(HostDto::from)
				.collect(toList());
		return dto;
	}
}
