package accepttest.dns.websocket.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.websocket.DnsQueryEventService;
import dk.mada.dns.websocket.dto.DnsQueryEventDto;
import dk.mada.dns.websocket.dto.EventTypeDto;
import fixture.dns.xbill.DnfFilterLocalHostLookup;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.undertow.websockets.jsr.DefaultWebSocketClientSslProvider;

@Tag("accept")
@QuarkusTest
public class EventOnDnsQueryTest {
	private static final Logger logger = LoggerFactory.getLogger(EventOnDnsQueryTest.class);
	private static final LinkedBlockingDeque<DnsQueryEventDto> MESSAGES = new LinkedBlockingDeque<>();

	@Inject private DnfFilterLocalHostLookup dnsFilterLookup;
	@TestHTTPResource(value = "/chat/event-test", ssl = true)
	URI uri;
	
	private static CountDownLatch websocketClientReady = new CountDownLatch(1);

	
	
	/**
	 * Tests that a DnsQueryEvent is sent over websocket as
	 * a result of a lookup.
	 */
	@Test
	public void testDnsLookup() throws Exception {
	    
	    
	    String wssUrl = uri.toString().replace("https:", "wss:");
	    URI secureUri = new URI("wss://localhost:8446/chat/event-test"); //wssUrl);
	    logger.info("CONNECT TO {}", secureUri);
//	    Thread.sleep(50_000);
	     try(Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, createClientConfig(), secureUri)) {
	    	 waitForWebsocketHello(session);
	    	 
	    	 dnsFilterLookup.serviceDnsLookup("mada.dk");

	    	 DnsQueryEventDto event = nextWebsocketMessage();
	    	 logger.info("Got event {}", event);
	    	 assertThat(event.type)
    			 .isEqualTo(EventTypeDto.PASSTHROUGH);
	    	 assertThat(event.hostname)
	    	 	.isEqualTo("mada.dk");
        }
	}


	
	// Without this - even though connection opened - server would never process
	// client connection until after first websocket event was sent.
	// Workaround by sending a hello to new clients.
	private void waitForWebsocketHello(Session session) throws InterruptedException {
	    logger.info("Wait for hello");
		if (!websocketClientReady.await(6, TimeUnit.SECONDS)) {
			 throw new IllegalStateException("Failed waiting for websocket client to connect");
		 }
		 logger.info("Websocket reported ready, isOpen:{}", session.isOpen());
	}

	private DnsQueryEventDto nextWebsocketMessage() throws InterruptedException {
		logger.info("Polling for websocket message");
		DnsQueryEventDto res = MESSAGES.poll(8, TimeUnit.SECONDS);
		if (res == null) {
			throw new IllegalStateException("Websocket message timeout");
		}
		return res;
	}
	
	private ClientEndpointConfig createClientConfig() throws Exception {
	    ClientEndpointConfig.Builder builder = ClientEndpointConfig.Builder.create();
	    var config = builder.build();
	    logger.info("Using client config {}", config);

	    try (InputStream is = getClass().getResourceAsStream("/cert/keystore.jks")) {
	        logger.info("Got cert stream {}", is);
	        config.getUserProperties().put(DefaultWebSocketClientSslProvider.SSL_CONTEXT, makeSslContextFromTrustStore(is, "secret"));
	    }
	    
	    return config;
	}

   private static SSLContext makeSslContextFromTrustStore(InputStream trustStoreInputStream, String trustStorePassword) throws Exception {
        try (InputStream is = trustStoreInputStream) {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(is, trustStorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(trustStore, trustStorePassword.toCharArray());
            
            TrustManagerFactory tm = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tm.init(trustStore);
            
            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(keyManagerFactory.getKeyManagers(), tm.getTrustManagers(), null);
            return context;
        }
    }
	
	@ClientEndpoint
	public static class Client extends Endpoint {
	    @Override
		@OnOpen
        public void onOpen(Session session, EndpointConfig config) {
			logger.info("Test client WebSocket connection on {}", session);
			final RemoteEndpoint remote = session.getBasicRemote();
	    
			session.addMessageHandler(String.class, new MessageHandler.Partial<String>() {
	                 public void onMessage(String text, boolean foo) {
	                     message(text);
	                 }
	             });
	    }
	    
		
		@OnMessage
		void message(String msg) {
			logger.info("WebSocket message {}", msg);
			
			if (DnsQueryEventService.HELLO_MESSAGE.equals(msg)) {
				logger.info("Got hello from server");
				websocketClientReady.countDown();
				return;
			}
			
			Jsonb jsonb = JsonbBuilder.create();
			MESSAGES.add(jsonb.fromJson(msg, DnsQueryEventDto.class));
		}
		
		@OnClose
		void onClose() {
			logger.info("Websocket client closing");
		}
		
		@Override
		@OnError
		public void onError(Session session, Throwable cause) {
			logger.error("Websocket client error", cause);
		}
	}
}
