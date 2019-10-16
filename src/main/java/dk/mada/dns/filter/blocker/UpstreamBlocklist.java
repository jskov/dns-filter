package dk.mada.dns.filter.blocker;

import java.util.Collection;

import dk.mada.dns.filter.Blockedlist;
import dk.mada.dns.filter.HostDomainNameMatcher;

public class UpstreamBlocklist extends HostDomainNameMatcher implements Blockedlist {

	public UpstreamBlocklist(Collection<String> hosts, Collection<String> domains) {
		super("Blocked", hosts, domains);
	}
}
