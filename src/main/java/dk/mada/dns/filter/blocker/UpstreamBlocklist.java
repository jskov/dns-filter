package dk.mada.dns.filter.blocker;

import java.util.Collection;

import dk.mada.dns.filter.Block;
import dk.mada.dns.filter.HostDomainNameMatcher;

public class UpstreamBlocklist extends HostDomainNameMatcher implements Block {
	public UpstreamBlocklist(Collection<String> hosts, Collection<String> domains) {
		super("Blocked", hosts, domains);
	}
}
