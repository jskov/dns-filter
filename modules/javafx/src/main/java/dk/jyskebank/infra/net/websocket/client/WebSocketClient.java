package dk.jyskebank.infra.net.websocket.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A websocket client implementation based on JDK11's builtin websocket API.
 * 
 * Use WebSocketClientBuilder to create a new instance.
 */
public class WebSocketClient {
	private static final long RECONNECT_MAX_DELAY_SECONDS = Duration.ofMinutes(5).toSeconds();
	private static final int RECONNECT_DELAY_INITIAL_SECONDS = 0;
	private static final int RECONNECT_DELAY_INCREMENT_SECONDS = 15;
	private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
	private final String pingMsg;
	private final HttpClient client;
	private final URI uri;
	private final Duration pingInterval;
	private Listener listener;
	private WebSocket websocket;
	private int reconnectDelaySeconds = RECONNECT_DELAY_INITIAL_SECONDS;
	private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
	private boolean closingDown;
	
	WebSocketClient(HttpClient client, URI uri, Duration pingInterval, String pingMsg) {
		this.client = client;
		this.uri = uri;
		this.pingInterval = pingInterval;
		this.pingMsg = pingMsg;
	}
	
	public void setListenerAndStart(Listener listener) {
		this.listener = Objects.requireNonNull(listener);
		makeNewWebsocket();
		
		backgroundExecutor.execute(this::backgroundServerPing);
	}
	
	private void backgroundServerPing() {
		logger.debug("Background PINGer (interval {}, message '{}') starting", pingInterval, pingMsg);
		while (true) {
			try {
				Thread.sleep(pingInterval.toMillis());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.debug("Ping thread interrupted, exiting");
			}
			if (backgroundExecutor.isShutdown()) {
				logger.debug("Background PINGer stopping");
				return;
			}
			if (websocket != null) {
				logger.debug("Sending background ping");
				websocket.sendPing(ByteBuffer.wrap(pingMsg.getBytes()));
			}
		}
	}
	
	public void sendText(String message) {
		if (websocket == null) {
			throw new WebSocketClientException("sendText called before websocket opened");
		}
		logger.trace("sendText: '{}'", message);
		try {
			websocket
				.sendText(message, true)
				.join();
		} catch (Exception e) {
			throw new WebSocketClientException("Failed to orderly close websocket", e);
		}
	}

	public void close() {
		logger.debug("close, normal closure");
		try {
			closingDown = true;
			backgroundExecutor.shutdownNow();
			backgroundExecutor.awaitTermination(2, TimeUnit.SECONDS);

			websocket
				.sendClose(WebSocket.NORMAL_CLOSURE, "")
				.join();
		} catch (Exception e) {
			throw new WebSocketClientException("Failed to orderly close websocket", e);
		} finally {
			websocket = null;
		}
	}

	public Optional<WebSocket> getWebSocket() {
		return Optional.ofNullable(websocket);
	}

	/**
	 * Called by SLH::onError and SLH::onClose
	 */
	void reopenConnectionOnError() {
		if (!closingDown) {
			int currentDelay = increaseReconnectTimeout();
			new Thread(() -> reopen(currentDelay)).start();
		}
	}

	
	private int increaseReconnectTimeout() {
		int currentTimeout = reconnectDelaySeconds;
		if (!hasReachedMaxReconnectTimeout()) {
			reconnectDelaySeconds += RECONNECT_DELAY_INCREMENT_SECONDS;
		}
		return currentTimeout;
	}

	boolean hasReachedMaxReconnectTimeout() {
		return reconnectDelaySeconds > RECONNECT_MAX_DELAY_SECONDS;
	}
	
	void resetReconnectTimeoutOnOpen() {
		reconnectDelaySeconds = RECONNECT_DELAY_INITIAL_SECONDS;
	}
	
	/**
	 * Called by SLH::onOpen
	 */
	void setWebSocket(WebSocket websocket) {
		this.websocket = websocket;
	}
	
	private void reopen(int afterDelaySeconds) {
		logger.info("Waiting a bit before trying to re-open websocket connection to {}", uri);
		
		if (afterDelaySeconds > 0) {
			try {
				Thread.sleep(Duration.ofSeconds(afterDelaySeconds).toMillis());
			} catch (InterruptedException e) {
				logger.warn("Interrupted while waiting to start over", e);
				Thread.currentThread().interrupt();
			}
		}
		
		makeNewWebsocket();
	}
	
	private void makeNewWebsocket() {
		try {
			logger.debug("Opening websocket at {}", uri);
			CompletableFuture<WebSocket> websocketFuture = client.newWebSocketBuilder()
					.buildAsync(uri, listener);
			this.websocket = websocketFuture.join();
		} catch (Exception e) {
			if (listener instanceof SimpleListenerHandler) {
				((SimpleListenerHandler)listener).onOpenError(e);
			} else {
				throw new WebSocketClientException("Failed to create initial websocket connection", e);
			}
		}
	}
}
