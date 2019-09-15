package accepttest.dns.websocket.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.websocket.dto.DnsQueryEventDto;
import fixture.dns.xbill.DnfFilterLocalHostLookup;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class EventOnDnsQueryTest {
	private static final Logger logger = LoggerFactory.getLogger(EventOnDnsQueryTest.class);
	private static final LinkedBlockingDeque<DnsQueryEventDto> MESSAGES = new LinkedBlockingDeque<>();

	@Inject private DnfFilterLocalHostLookup dnsFilterLookup;
	@TestHTTPResource("/chat/event-test")
	URI uri;

	/**
	 * Tests that a DnsQueryEvent is sent over websocket as
	 * a result of a lookup.
	 */
	@Test
	public void testDnsLookup() throws Exception {
	     try(Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
	    	 dnsFilterLookup.serviceDnsLookup("mada.dk");

	    	 DnsQueryEventDto event = nextWebsocketMessage();
	    	 logger.info("Got event {}", event);
	    	 assertThat(event.ip)
	    			 .isEqualTo("185.17.217.100");
        }
	}

	private DnsQueryEventDto nextWebsocketMessage() throws InterruptedException {
		DnsQueryEventDto res = MESSAGES.poll(10, TimeUnit.SECONDS);
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
		}
		
		@OnMessage
		void message(String msg) {
			logger.info("WebSocket message {}", msg);
			Jsonb jsonb = JsonbBuilder.create();
			MESSAGES.add(jsonb.fromJson(msg, DnsQueryEventDto.class));
		}
	}
}
