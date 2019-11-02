package dk.mada.dns.config;

public class Host implements Comparable<Host> {
	private final String host;
	private final String reason;

	public Host(String host, String reason) {
		this.host = host;
		this.reason = reason;
	}

	public String getHost() {
		return host;
	}

	public String getReason() {
		return reason;
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
		Host other = (Host) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}

	@Override
	public int compareTo(Host o) {
		return host.compareTo(o.host);
	}
}
