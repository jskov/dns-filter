package dk.mada.dns.lookup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.mada.dns.filter.Blacklist;
import dk.mada.dns.filter.Whitelist;
import dk.mada.dns.filter.blocker.BlockedListCacher;
import dk.mada.dns.resolver.UpstreamResolver;

@ApplicationScoped
public class FilteredLookup {
	@Inject private UpstreamResolver resolver;
	@Inject private BlockedListCacher fetchLists;
	
	private LookupEngine engine;
	
	public LookupResult lookup(Query q) {
		return getEngine().lookup(q);
	}
	
	public LookupResult makeBlockedReply(Query q, String toggle) {
		return getEngine().makeBlockedReply(q, LookupState.TOGGLE, toggle);
	}
	
	private synchronized LookupEngine getEngine() {
		if (engine == null) {
			var blockedlist = fetchLists.get();
			Whitelist whitelist = h -> false;
			Blacklist blacklist = h -> false;
			engine = new LookupEngine(resolver, blockedlist, blacklist, whitelist);
		}
		return engine;
	}

}
