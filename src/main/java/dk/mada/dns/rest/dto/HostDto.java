package dk.mada.dns.rest.dto;

import dk.mada.dns.config.Host;

/**
 * DTO for a host blacklisted or whitelisted.
 */
public class HostDto {
	public String host;
	public String reason;
	
	public static HostDto from(Host h) {
		var dto = new HostDto();
		dto.host = h.getHost();
		dto.reason = h.getReason();
		return dto;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HostDto other = (HostDto) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}
}
