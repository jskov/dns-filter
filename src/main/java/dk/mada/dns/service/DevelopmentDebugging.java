package dk.mada.dns.service;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	private Set<String> outputForHostnames = new HashSet<>();
	private int printNextRequests;
	
	public void devOutputWireData(String host, String title, ByteBuffer bb) {
		if (outputForHostnames.contains(host)) {
			Hexer.printForDevelopment(title, bb, Collections.emptySet());
		}
	}
	
	public void startOutputForHost(String host) {
		outputForHostnames.add(host);
	}

	public void stopOutputForHost(String host) {
		outputForHostnames.remove(host);
	}

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
