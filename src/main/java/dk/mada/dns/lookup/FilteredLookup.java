package dk.mada.dns.lookup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.mada.dns.filter.ConfiguredDenied;
import dk.mada.dns.filter.ConfiguredAllowed;
import dk.mada.dns.filter.blocker.BlockedListCacher;
import dk.mada.dns.resolver.DefaultResolver;

@ApplicationScoped
public class FilteredLookup {
	@Inject private DefaultResolver resolver;
	@Inject private BlockedListCacher fetchLists;
	@Inject private ConfiguredDenied denied;
	@Inject private ConfiguredAllowed allowed;
	
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
			engine = new LookupEngine(resolver, blockedlist, denied, allowed);
		}
		return engine;
	}
}
