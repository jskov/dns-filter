package dk.mada.dns;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.service.DnsLookupService;
import dk.mada.dns.service.UDPServer;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

/**
 * Main application.
 * 
 * Sets up UDP service for incoming DNS calls.
 */
@ApplicationScoped
public class Application {
    public static final int DNS_LISTENING_PORT = 8053;

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private UDPServer server;

    @Inject private DnsLookupService resolver;
    
    void onStart(@Observes StartupEvent ev) {
        logger.info("The application is starting...");
    
        server = new UDPServer(DNS_LISTENING_PORT);
		server.setPacketHandler(resolver);
        server.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
    	logger.info("Container wants to shut down...");
    	server.stop();
    }
}
