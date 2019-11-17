package dk.mada.dns.config;

public class BlockedItem implements Comparable<BlockedItem> {
	private final String name;
	private final String reason;

	public BlockedItem(String name, String reason) {
		this.name = name;
		this.reason = reason;
	}

	public String getName() {
		return name;
	}

	public String getReason() {
		return reason;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		BlockedItem other = (BlockedItem) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(BlockedItem o) {
		return name.compareTo(o.name);
	}
}
