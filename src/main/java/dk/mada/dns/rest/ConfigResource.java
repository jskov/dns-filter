package dk.mada.dns.rest;


import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import dk.mada.dns.config.Configuration;
import dk.mada.dns.rest.dto.DomainDto;
import dk.mada.dns.rest.dto.FilterDto;
import dk.mada.dns.rest.dto.HostDto;
import static dk.mada.dns.rest.DomainToDtoMapper.*;

/**
 * Resource for access to configuration.
 */
@Path("/config")
public class ConfigResource {
	@Inject private Configuration configuration;
	
	@Path("filter")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public FilterDto getFilter() {
		return filterDto(configuration);
	}
	
	@Path("filter/whitelist/host")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HostDto> getWhitelistedHosts() {
		return configuration.getAllowedHosts().stream()
				.map(h -> hostDto(h))
				.collect(toList());
    }

	@Path("filter/whitelist/host/{hostname}")
	@PUT
	public void whitelistHost(@PathParam("hostname") String hostname, @QueryParam("reason") @DefaultValue("") String reason) {
		configuration.allowHost(hostname, reason);
	}

	@Path("filter/whitelist/domain")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DomainDto> getWhitelistedDomains() {
		return configuration.getAllowedDomains().stream()
				.map(d -> domainDto(d))
				.collect(toList());
    }

	@Path("filter/whitelist/domain/{domainname}")
	@PUT
	public void whitelistDomain(@PathParam("domainname") String domainname, @QueryParam("reason") @DefaultValue("") String reason) {
		configuration.allowDomain(domainname, reason);
	}

	@Path("filter/deny/host")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HostDto> getDeniedHosts() {
		return configuration.getDeniedHosts().stream()
				.map(h -> hostDto(h))
				.collect(toList());
    }

	@Path("filter/deny/host/{hostname}")
	@PUT
	public void denyHost(@PathParam("hostname") String hostname, @QueryParam("reason") @DefaultValue("") String reason) {
		configuration.denyHost(hostname, reason);
	}

	@Path("filter/deny/domain")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DomainDto> getDeniedDomains() {
		return configuration.getDeniedDomains().stream()
				.map(d -> domainDto(d))
				.collect(toList());
    }

	@Path("filter/deny/domain/{domainname}")
	@PUT
	public void denyDomain(@PathParam("domainname") String domainname, @QueryParam("reason") @DefaultValue("") String reason) {
		configuration.denyDomain(domainname, reason);
	}
}