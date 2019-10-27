package dk.mada.dns.service;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	
	enum Action {
		BYPASS,
		ECHO
	}
	
	private Map<String, Action> outputForHostnames = new HashMap<>();
	
	public void devOutputWireData(String hostname, String title, ByteBuffer bb) {
		if (isEchoOutputForHost(hostname) || isBypassForHost(hostname)) {
			Hexer.printForDevelopment(title, bb, Collections.emptySet());
		}
	}
	
	public boolean isEchoOutputForHost(String hostname) {
		return Action.ECHO == outputForHostnames.get(hostname);
	}

	public boolean isBypassForHost(String hostname) {
		return Action.BYPASS == outputForHostnames.get(hostname);
	}

	public void startEchoForHost(String hostname) {
		outputForHostnames.put(hostname, Action.ECHO);
	}

	public void startBypassForHost(String hostname) {
		outputForHostnames.put(hostname, Action.ECHO);
	}

	public void stopActionForHost(String hostname) {
		outputForHostnames.remove(hostname);
	}
}
