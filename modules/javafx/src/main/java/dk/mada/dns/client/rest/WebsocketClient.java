package dk.mada.dns.client.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jyskebank.infra.net.websocket.client.SimpleListener;
import dk.jyskebank.infra.net.websocket.client.WebSocketClient;
import dk.jyskebank.infra.net.websocket.client.WebSocketClientBuilder;

@ApplicationScoped
public class WebsocketClient implements SimpleListener {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketClient.class);
    private WebSocketClient client;
    
    public void connect() {
//        String url = "wss://10.0.0.10/chat";
        String url = "wss://127.0.0.1:8443/chat/app";
        
        logger.info("Opening connection to {}", url);
        
        try (InputStream is = getClass().getResourceAsStream("/trustca.jks")) {
            client = new WebSocketClientBuilder(url)
                    .simpleListener(this)
                    .trustStore(is, "changeit")
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to open trust store", e);
        }
    }

    @Override
    public void onText(String message) {
        logger.info("Text {}", message);
    }

    @Override
    public void onClose(int statusCode, String reason) {
        logger.info("Closed! {}:{}", statusCode, reason);
    }

    @Override
    public void onConnectionChange(boolean isOpen) {
        logger.info("Websocket is now {}", isOpen ? "open" : "closed");
    }
}
