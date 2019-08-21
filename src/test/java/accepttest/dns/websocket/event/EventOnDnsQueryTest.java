package accepttest.dns.websocket.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import dk.mada.dns.Application;
import dk.mada.dns.websocket.dto.EventDto;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class EventOnDnsQueryTest {
	private static final Logger logger = LoggerFactory.getLogger(EventOnDnsQueryTest.class);
	private static final LinkedBlockingDeque<EventDto> MESSAGES = new LinkedBlockingDeque<>();

	@TestHTTPResource("/chat/event-test")
	URI uri;

	/**
	 * Tests that a websocket event is sent on a DNS request.
	 */
	@Test
	public void testDnsLookup() throws Exception {
	     try(Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
	    	 
//	    	 assertThat(nextWebsocketMessage())
//	    	 	.isEqualTo("CONNECT");

	    	 makeDnsLookup("github.com");

	    	 EventDto event = nextWebsocketMessage();
	    	 logger.info("Got event {}", event);
	    	 assertThat(event.reply)
	    			 .contains("github.com.");
        }
	}

	private EventDto nextWebsocketMessage() throws InterruptedException {
		return MESSAGES.poll(3, TimeUnit.SECONDS);
	}
	
	private void makeDnsLookup(String lookupHostname) throws Exception {
		Lookup lookup = new Lookup(lookupHostname);
		lookup.setResolver(getLocalhostResolver());
		lookup.setCache(null);
		lookup.setSearchPath(new String[] {});

		Record[] res = lookup.run();
		assertThat(lookup.getResult()).isEqualTo(0);
		assertThat(res).extracting(r -> r.getName().toString()).contains("github.com.");
	}

	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
		localhostResolver.setPort(Application.DNS_LISTENING_PORT);
		return localhostResolver;
	}

	@ClientEndpoint
	public static class Client {

		@OnOpen
		public void open() {
//			MESSAGES.add("CONNECT");
		}

		@OnMessage
		void message(String msg) {
			Jsonb jsonb = JsonbBuilder.create();
			MESSAGES.add(jsonb.fromJson(msg, EventDto.class));
		}

	}
}
