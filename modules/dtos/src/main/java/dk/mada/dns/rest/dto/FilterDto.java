package dk.mada.dns.rest.dto;

import java.util.List;

public class FilterDto {
	public List<DomainDto> blacklistedDomains = List.of();
	public List<HostDto> blacklistedHosts = List.of();
	public List<DomainDto> whitelistedDomains = List.of();
	public List<HostDto> whitelistedHosts = List.of();
}
