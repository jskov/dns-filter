package dk.mada.dns.wire.model;

/**
 * DNS Name entry, built from individual labels and/or compression.
 * 
 * From https://www.ietf.org/rfc/rfc1035.txt, sections:
 *  3.3. Standard RRs
 *  4.1.4. Message compression
 */
public class DnsName {
	private String name;
	
	private DnsName(String name) {
		this.name = name;
	}
	
	public DnsName fromName(String name) {
		return new DnsName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
