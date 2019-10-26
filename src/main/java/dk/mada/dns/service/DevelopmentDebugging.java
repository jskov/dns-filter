package dk.mada.dns.service;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import dk.mada.dns.util.Hexer;

/**
 * Hooks into service that allows
 * packages to the printed to console for use
 * in test code.
 * 
 * Activate by first running a query for:
 *  dns-echo.hostname
 * 
 * Filtering is also disabled.
 */
@ApplicationScoped
public class DevelopmentDebugging {
	private Set<String> outputForHostnames = new HashSet<>();
	
	public void devOutputWireData(String hostname, String title, ByteBuffer bb) {
		if (isEchoOutputForHost(hostname)) {
			Hexer.printForDevelopment(title, bb, Collections.emptySet());
		}
	}
	
	public boolean isEchoOutputForHost(String hostname) {
		return outputForHostnames.contains(hostname);
	}
	
	public void startOutputForHost(String hostname) {
		outputForHostnames.add(hostname);
	}

	public void stopOutputForHost(String hostname) {
		outputForHostnames.remove(hostname);
	}
}
