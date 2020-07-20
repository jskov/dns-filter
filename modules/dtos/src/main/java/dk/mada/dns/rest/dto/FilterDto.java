package dk.mada.dns.rest.dto;

import java.util.List;

public class FilterDto {
	public List<DomainDto> deniedDomains = List.of();
	public List<HostDto> deniedHosts = List.of();
	public List<DomainDto> allowedDomains = List.of();
	public List<HostDto> allowedHosts = List.of();
}
