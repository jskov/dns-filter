package dk.mada.dns.service;

import javax.enterprise.context.ApplicationScoped;

import dk.mada.dns.util.Hexer;
import dk.mada.dns.wire.model.DnsRequest;

/**
 * Development hooks into service that allows
 * packages to the printed to console for use
 * in code.
 * 
 * Trigger output for 20 packages by making a
 * request for mada.dk.
 */
@ApplicationScoped
public class DevelopmentDebugging {
	private int printNextRequests;
	
	public void devOutputRequest(DnsRequest request) {
		if (printNextRequests > 0) {
			Hexer.printForDevelopment(request);
			printNextRequests--;
		}

		if ("mada.dk".equals(request.getQuestion().getName().getName())) {
			printNextRequests = 20;
		}
	}
}
