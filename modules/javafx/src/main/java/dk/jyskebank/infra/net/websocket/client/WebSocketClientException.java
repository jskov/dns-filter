package dk.jyskebank.infra.net.websocket.client;

/**
 * Exception wrapping all other exceptions thrown by WebSocketClient code.
 */
public class WebSocketClientException extends RuntimeException {
	public WebSocketClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebSocketClientException(String message) {
		super(message);
	}
}
