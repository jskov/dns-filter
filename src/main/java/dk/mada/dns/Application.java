package dk.mada.dns;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

/**
 * Main application.
 */
@ApplicationScoped
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    void onStart(@Observes StartupEvent ev) {               
        logger.info("The application is starting...");
    }

    void onStop(@Observes ShutdownEvent ev) {
    	logger.info("Container wants to shut down...");
    }
}
