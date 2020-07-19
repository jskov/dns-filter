package dk.mada.dns.websocket;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.websocket.dto.DnsQueryEventDto;

// TODO: testing see https://github.com/quarkusio/quarkus-quickstarts/blob/master/using-websockets/src/test/java/org/acme/websocket/ChatTestCase.java

@ApplicationScoped
@ServerEndpoint(value = "/chat/{username}", encoders = {
        MessageEncoder.class
})
public class DnsQueryEventService {
	private static final Logger logger = LoggerFactory.getLogger(DnsQueryEventService.class);
	
	public static final String HELLO_MESSAGE = "Hello DNS client";
	
	private Map<String, Session> sessions = new ConcurrentHashMap<>();

	public void close() {
		logger.info("Closing websocket event service");
		sessions.values().forEach(s -> closeSession(s));
	}

	private void closeSession(Session s) {
		try {
			s.close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		sessions.put(username, session);
		logger.info("User {} joined with {}", username, session);
		
		try {
			session.getAsyncRemote().sendObject(HELLO_MESSAGE);
			logger.debug("Hello sent to new client");
		} catch (Exception e) {
			logger.warn("Failed sending ping to client", e);
		}
	}

	@OnClose
	public void onClose(Session session, @PathParam("username") String username) {
		sessions.remove(username);
		logger.info("User {} closed {}", username, session);
	}

	@OnError
	public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
		sessions.remove(username);
		logger.info("User {} failed {}", username, session, throwable);
	}

	@OnMessage
	public void onMessage(String message, @PathParam("username") String username) {
		logger.info("User {} message {}", username, message);
		
		broadcast(message);
	}

	private void broadcast(String message) {
		DnsQueryEventDto dto = new DnsQueryEventDto();
		dto.hostname = ("NAME: " + message);
		logger.info("From {} to {}", message, dto);
		
		broadcast(dto);
	}
	
	// FIXME: should happen async, so the caller can return asap
	public void broadcast(DnsQueryEventDto dto) {
		sessions.values().forEach(s -> {
			s.getAsyncRemote().sendObject(dto, result -> {
				if (result.getException() != null) {
					System.out.println("Unable to send message: " + result.getException());
				}
			});
		});
	}
}
