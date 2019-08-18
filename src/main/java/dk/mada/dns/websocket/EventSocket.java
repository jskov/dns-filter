package dk.mada.dns.websocket;

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

import dk.mada.dns.rest.dto.EventDto;

@ApplicationScoped
@ServerEndpoint(value = "/chat/{username}", encoders = {
        MessageEncoder.class
})
public class EventSocket {
	private static final Logger logger = LoggerFactory.getLogger(EventSocket.class);
	Map<String, Session> sessions = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		sessions.put(username, session);
		logger.info("User {} joined with {}", username, session);
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
		EventDto dto = new EventDto();
		dto.setName("NAME: " + message);
		logger.info("From {} to {}", message, dto);
		
		sessions.values().forEach(s -> {
			s.getAsyncRemote().sendObject(dto, result -> {
				if (result.getException() != null) {
					System.out.println("Unable to send message: " + result.getException());
				}
			});
		});
	}
}
