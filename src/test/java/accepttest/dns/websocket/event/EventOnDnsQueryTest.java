package accepttest.dns.websocket.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.websocket.dto.DnsQueryEventDto;
import fixture.dns.xbill.DnsPayloadHelper;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class EventOnDnsQueryTest {
	private static final Logger logger = LoggerFactory.getLogger(EventOnDnsQueryTest.class);
	private static final LinkedBlockingDeque<DnsQueryEventDto> MESSAGES = new LinkedBlockingDeque<>();

	@Inject private DnsPayloadHelper dnsHelper;
	@TestHTTPResource("/chat/event-test")
	URI uri;
	
	private static CountDownLatch websocketClientReady = new CountDownLatch(1);

	/**
	 * Tests that a DnsQueryEvent is sent over websocket as
	 * a result of a lookup.
	 */
	@Test
	public void testDnsLookup() throws Exception {
	     try(Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
	    	 
	    	 if (!websocketClientReady.await(3, TimeUnit.SECONDS)) {
	    		 throw new IllegalStateException("Failed waiting for websocket client to connect");
	    	 }
	    	 logger.info("Websocket reported ready, isOpen:{}", session.isOpen());
	    	 
	    	 
	    	 dnsHelper.serviceDnsLookup("mada.dk");

	    	 DnsQueryEventDto event = nextWebsocketMessage();
	    	 logger.info("Got event {}", event);
	    	 assertThat(event.ip)
	    			 .isEqualTo("185.17.217.100");
        }
	}

	private DnsQueryEventDto nextWebsocketMessage() throws InterruptedException {
		logger.info("Polling for websocket message");
		DnsQueryEventDto res = MESSAGES.poll(8, TimeUnit.SECONDS);
		if (res == null) {
			throw new IllegalStateException("Websocket message timeout");
		}
		return res;
	}
	
	@ClientEndpoint
	public static class Client {
		@OnOpen
		void onOpen(Session session) {
			logger.info("Test client WebSocket connection on {}", session);
			websocketClientReady.countDown();
		}
		
		@OnMessage
		void message(String msg) {
			logger.info("WebSocket message {}", msg);
			Jsonb jsonb = JsonbBuilder.create();
			MESSAGES.add(jsonb.fromJson(msg, DnsQueryEventDto.class));
		}
		
		@OnClose
		void onClose() {
			logger.info("Websocket client closing");
		}
		
		@OnError
		void onError(Session session, Throwable cause) {
			logger.error("Websocket client error", cause);
		}
	}
}
