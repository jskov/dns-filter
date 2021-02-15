package dk.jyskebank.infra.net.websocket.client;

/**
 * A simplified WebSocket listener.
 * 
 * Leaves handling of errors and WebSocket.Listener contract adherence to
 * SimpleListenerHandler.
 */
public interface SimpleListener {
	/**
	 * Called when the websocket is opened.
	 */
	default void onOpen() {
	}
	
	
	/**
	 * Called when the websocket is opened.
	 */
	default void onOpen(WebSocketClient client) {
		onOpen();
	}

	
	/**
	 * Called when the server sends a message.
	 */
	void onText(String message);
	
	
	/**
	 * Called when the websocket is closed.
	 */
	void onClose(int statusCode, String reason);
	
	/**
	 * Called when the websocket connection state
	 * changes.
	 * 
	 * This is an advisory callback.
	 * The client should not make any connection-related operations
	 * based on this (use onOpen and onClose for that).
	 * Use the callback for something like GUI feedback.
	 *
	 * @param isOpen true if the connection is open, otherwise false
	 */
	void onConnectionChange(boolean isOpen);
}
