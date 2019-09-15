package dk.mada.dns.lookup;

import dk.mada.dns.wire.model.DnsReply;

public class LookupResult {

	private DnsReply reply;

	public LookupState getState() {
		return LookupState.FAILED;
	}

	public void setReply(DnsReply reply) {
		this.reply = reply;
	}

}
