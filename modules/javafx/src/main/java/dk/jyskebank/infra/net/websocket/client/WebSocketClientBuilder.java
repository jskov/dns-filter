package dk.jyskebank.infra.net.websocket.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.WebSocket.Listener;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * A builder for WebSocketClient.
 */

public class WebSocketClientBuilder {
	private final URI uri;
	private Listener listener;
	private SimpleListener simpleListener;
	private SSLContext sslContext;
	private Consumer<Builder> httpClientConfigurator;
	private Duration connectTimeout = Duration.ofSeconds(10);
	private String pingMessage = "ping";
	private Duration pingInterval = Duration.ofMinutes(1);
	
	public WebSocketClientBuilder(URI uri) {
		this.uri = Objects.requireNonNull(uri);
	}

	public WebSocketClientBuilder(String uri) {
		this(URI.create(Objects.requireNonNull(uri)));
	}

	/**
	 * Adds a fully compliant WebSocket.Listener to the client.
	 * 
	 * @param listener a fully compliant WebSocket.Listener.
	 */
	public WebSocketClientBuilder listener(Listener listener) {
		this.listener = Objects.requireNonNull(listener);
		return this;
	}
	
	/**
	 * Adds a simple listener to the client.
	 * 
	 * @param listener a simple listener.
	 */
	public WebSocketClientBuilder simpleListener(SimpleListener simpleListener) {
		this.simpleListener = Objects.requireNonNull(simpleListener);
		return this;
	}

	/**
	 * Load trust store from resource path, with the given password.
	 * 
	 * The resource will be loaded from the WebSocketClientBuilder, so in a modular
	 * context this may not work.
	 * 
	 * @param trustResourcePath Path to load trust store from.
	 * @param trustPassword Password of trust store.
	 */
	public WebSocketClientBuilder trustStore(String trustResourcePath, String trustPassword) {
		try (InputStream trust = getClass().getResourceAsStream(Objects.requireNonNull(trustResourcePath))) {
			return trustStore(trust, trustPassword);
		} catch (IOException e) {
			throw new WebSocketClientException("Failed to open input stream for trust resource " + trustResourcePath,  e);
		}
	}

	/**
	 * Load trust store from input stream, with the given password.
	 * 
	 * @param trust Stream of trust store. 
	 * @param trustPassword Password of trust store.
	 */
	public WebSocketClientBuilder trustStore(InputStream trust, String trustPassword) {
		sslContext = makeSslContextFromTrustStore(
				Objects.requireNonNull(trust, "Trust cannot be null"),
				Objects.requireNonNull(trustPassword, "Password cannot be null"));
		return this;
	}

	/**
	 * Adds a HttpClient.Builder configurator which is called just
	 * before the HttpClient is created (from the builder).
	 * 
	 * @param httpClientConfigurator http client configurator.
	 */
	public WebSocketClientBuilder httpClientConfigurator(Consumer<HttpClient.Builder> httpClientConfigurator) {
		this.httpClientConfigurator = Objects.requireNonNull(httpClientConfigurator);
		return this;
	}

	/**
	 * Specifies a full SSLContext instead of the trustStore loaders.
	 *
	 * This method allows a client to use a less strict SSLContext than the
	 * one created by the trustStore loaders.
	 *
	 * @param sslContext a ready SSL context
	 */
	public WebSocketClientBuilder sslContext(SSLContext sslContext) {
		this.sslContext = Objects.requireNonNull(sslContext);
		return this;
	}

	/**
	 * Specify a client-specific ping message.
	 * 
	 * @param pingMessage client-specific message
	 */
	public WebSocketClientBuilder pingMessage(String pingMessage) {
		this.pingMessage = Objects.requireNonNull(pingMessage);
		return this;
	}
	
	/**
	 * Interval between pings sent to the server.
	 * 
	 * @param pingInterval interval between pings sent to the server.
	 */
	public WebSocketClientBuilder pingInterval(Duration pingInterval) {
		this.pingInterval  = Objects.requireNonNull(pingInterval);
		return this;
	}
	
	/**
	 * Creates the WebSocketClient from the provided arguments.
	 * 
	 * @return a new client where the connection has been created.
	 */
	public WebSocketClient build() {
		Builder builder = HttpClient.newBuilder()
				.connectTimeout(connectTimeout);
//				.sslContext(sslContext);
		
		if (httpClientConfigurator != null) {
			httpClientConfigurator.accept(builder);
		}
		
		HttpClient client = builder.build();

		WebSocketClient wsClient = new WebSocketClient(client, uri, pingInterval, pingMessage);
		
		if (listener != null) {
			wsClient.setListenerAndStart(listener);
		} else if (simpleListener != null) {
			wsClient.setListenerAndStart(new SimpleListenerHandler(wsClient, simpleListener));
		} else {
			throw new IllegalArgumentException("A (simple)listener must be provided");
		}
		
		return wsClient;
	}
	
	private static SSLContext makeSslContextFromTrustStore(InputStream trustStoreInputStream, String trustStorePassword) {
		try (InputStream is = trustStoreInputStream) {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(is, trustStorePassword.toCharArray());

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(trustStore, trustStorePassword.toCharArray());
			
			TrustManagerFactory tm = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tm.init(trustStore);
			
			SSLContext context = SSLContext.getInstance("TLSv1.2");
			context.init(keyManagerFactory.getKeyManagers(), tm.getTrustManagers(), null);
			return context;
		} catch (Exception e) {
			throw new WebSocketClientException("Failed to create SSL context",  e);
		}
	}
}
