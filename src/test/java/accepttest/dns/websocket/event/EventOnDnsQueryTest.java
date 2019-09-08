package accepttest.dns.websocket.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
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
import dk.mada.dns.websocket.dto.DnsQueryEventDto;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@Tag("accept")
@QuarkusTest
public class EventOnDnsQueryTest {
	private static final Logger logger = LoggerFactory.getLogger(EventOnDnsQueryTest.class);
	private static final LinkedBlockingDeque<DnsQueryEventDto> MESSAGES = new LinkedBlockingDeque<>();

	@TestHTTPResource("/chat/event-test")
	URI uri;

	/**
	 * Tests that a DnsQueryEvent is sent over websocket as
	 * a result of a lookup.
	 */
	@Test
	public void testDnsLookup() throws Exception {
	     try(Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
	    	 makeDnsLookup("mada.dk");

	    	 DnsQueryEventDto event = nextWebsocketMessage();
	    	 logger.info("Got event {}", event);
	    	 assertThat(event.ip)
	    			 .isEqualTo("185.17.217.100");
        }
	}

	private DnsQueryEventDto nextWebsocketMessage() throws InterruptedException {
		return MESSAGES.poll(10, TimeUnit.SECONDS);
	}
	
	private void makeDnsLookup(String lookupHostname) throws Exception {
		Lookup lookup = new Lookup(lookupHostname);
		lookup.setResolver(getLocalhostResolver());
		lookup.setCache(null);
		lookup.setSearchPath(new String[] {});

		Record[] res = lookup.run();
		assertThat(lookup.getResult()).isEqualTo(0);
		assertThat(res).extracting(r -> r.getName().toString()).contains(lookupHostname+".");
	}

	private SimpleResolver getLocalhostResolver() throws UnknownHostException, TextParseException {
		SimpleResolver localhostResolver = new SimpleResolver("localhost");
		localhostResolver.setPort(Application.DNS_LISTENING_PORT);
		return localhostResolver;
	}

	@ClientEndpoint
	public static class Client {

		@OnMessage
		void message(String msg) {
			Jsonb jsonb = JsonbBuilder.create();
			logger.info("Got message: {}", msg);
			MESSAGES.add(jsonb.fromJson(msg, DnsQueryEventDto.class));
		}

	}
}
