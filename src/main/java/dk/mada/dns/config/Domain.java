package dk.mada.dns.config;

public class Domain implements Comparable<Domain> {
	private final String domain;
	private final String reason;

	public Domain(String domain, String reason) {
		this.domain = domain;
		this.reason = reason;
	}

	public String getDomain() {
		return domain;
	}

	public String getReason() {
		return reason;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
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
		Domain other = (Domain) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		return true;
	}

	@Override
	public int compareTo(Domain o) {
		return domain.compareTo(o.domain);
	}
}
