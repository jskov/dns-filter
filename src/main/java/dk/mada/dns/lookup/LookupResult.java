package dk.mada.dns.lookup;

import dk.mada.dns.wire.model.DnsReply;

public class LookupResult {
	private DnsReply reply;
	private LookupState state;

	public LookupState getState() {
		return state;
	}

	public void setState(LookupState state) {
		this.state = state;
	}

	public void setReply(DnsReply reply) {
		this.reply = reply;
	}

	@Override
	public String toString() {
		return "LookupResult [state=" + state + ", reply=" + reply + "]";
	}
}
