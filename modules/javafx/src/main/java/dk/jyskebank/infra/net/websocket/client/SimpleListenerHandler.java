package dk.jyskebank.infra.net.websocket.client;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler to be used with SimpleListener.
 * 
 * It takes care of fulfilling the WebSocket.Listener contract, while
 * providing a simple listener interface to clients.
 * 
 * On error, a new connection will be created after a delay.
 * This includes error on initial connection (via onOpenError).
 */
public class SimpleListenerHandler implements WebSocket.Listener {
	private static final Logger logger = LoggerFactory.getLogger(SimpleListenerHandler.class);

	private final WebSocketClient client;
	private final SimpleListener listener;

	private List<CharSequence> parts = new ArrayList<>();
	private CompletableFuture<?> accumulatedMessage = new CompletableFuture<>();

	public SimpleListenerHandler(WebSocketClient client, SimpleListener listener) {
		this.client = client;
		this.listener = listener;
	}

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence message, boolean last) {
		WebSocket.Listener.super.onText(webSocket, message, last);
		parts.add(message);

		logger.trace("Got partial text: '{}'", message);
		if (last) {
			listener.onText(String.join("", parts));
			parts = new ArrayList<>();
			accumulatedMessage.complete(null);
			CompletionStage<?> cf = accumulatedMessage;
			accumulatedMessage = new CompletableFuture<>();
			return cf;
		}
		return accumulatedMessage;
	}

	@Override
	public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
		logger.error("OnBinary not implemented");
		return WebSocket.Listener.super.onBinary(webSocket, data, last);
	}
	
	@Override
	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		logger.trace("OnClose");
		listener.onConnectionChange(false);
		listener.onClose(statusCode, reason);
		client.reopenConnectionOnError();
		return null;
	}
	
	@Override
	public void onError(WebSocket webSocket, Throwable error) {
		logger.warn("WebSocket failure: {}", error.getMessage(), error);
		listener.onConnectionChange(false);
		client.reopenConnectionOnError();
	}

	// Special for this implementation - called when (new) connection to server fails
	public void onOpenError(Exception error) {
		listener.onConnectionChange(false);
		if (client.hasReachedMaxReconnectTimeout()) {
			logger.warn("WebSocket connection failure: {}", error.getMessage(), error);
		}
		client.reopenConnectionOnError();
	}

	@Override
	public void onOpen(WebSocket webSocket) {
		WebSocket.Listener.super.onOpen(webSocket);
		logger.trace("OnOpen {}", webSocket);
		client.setWebSocket(webSocket);
		client.resetReconnectTimeoutOnOpen();
		listener.onConnectionChange(true);
		listener.onOpen(client);
	}
	
	@Override
	public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
		logger.trace("onPing");
		return WebSocket.Listener.super.onPing(webSocket, message);
	}

	@Override
	public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
		logger.trace("onPong");
		return WebSocket.Listener.super.onPing(webSocket, message);
	}
}
