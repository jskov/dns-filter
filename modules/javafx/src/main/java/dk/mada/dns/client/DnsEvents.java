package dk.mada.dns.client;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jyskebank.infra.net.websocket.client.SimpleListener;

@ApplicationScoped
public class DnsEvents implements SimpleListener {
	private static final Logger logger = LoggerFactory.getLogger(DnsEvents.class);

	@Override
	public void onText(String message) {
		logger.info("Got {}", message);
	}

	@Override
	public void onClose(int statusCode, String reason) {
		logger.info("CLOSE {} : {}", statusCode, reason);
	}

	@Override
	public void onConnectionChange(boolean isOpen) {
		logger.info("Change connection, isOpen: {}", isOpen);
	}

}
