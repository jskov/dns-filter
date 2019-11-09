package dk.mada.dns.wire.model;

/**
 * DNS Name entry, built from individual labels and/or compression.
 * 
 * From https://www.ietf.org/rfc/rfc1035.txt, sections:
 *  3.3. Standard RRs
 *  4.1.4. Message compression
 *  
 * Also note reservations:
 *  https://www.iana.org/assignments/special-use-domain-names/special-use-domain-names.xhtml
 * And how to handle them:
 *  https://tools.ietf.org/html/rfc6761
 */
public class DnsName {
	private String name;
	
	private DnsName(String name) {
		this.name = name;
	}
	
	public static DnsName fromName(String name) {
		return new DnsName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DnsName [name=" + name + "]";
	}
}
