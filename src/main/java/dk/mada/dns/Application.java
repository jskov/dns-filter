package dk.mada.dns;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.filter.blocker.BlockedListCacher;
import dk.mada.dns.service.DnsLookupService;
import dk.mada.dns.service.UDPServer;
import dk.mada.dns.websocket.DnsQueryEventService;
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

    @Inject private DnsLookupService resolver;
    @Inject private DnsQueryEventService websocketService;
    @Inject private BlockedListCacher blockedListCacher;

    private UDPServer server;
    
    void onStart(@Observes StartupEvent ev) {
        logger.info("The application is starting...");
    
        blockedListCacher.preloadCache();
        
        server = new UDPServer(DNS_LISTENING_PORT);
		server.setPacketHandler(resolver);
        server.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
    	logger.info("Container wants to shut down...");
    	server.stop();
    	websocketService.close();
    }
}
