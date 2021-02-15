package dk.mada.dns.client;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jyskebank.infra.net.websocket.client.WebSocketClient;
import dk.jyskebank.infra.net.websocket.client.WebSocketClientBuilder;
import dk.mada.dns.client.gui.events.ClientGui;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter {
	private static final Logger logger = LoggerFactory.getLogger(Starter.class);
	@Inject FXMLLoader fxmlLoader;
	
	@Inject DnsEvents dnsEvents;
	
	public void start(Stage stage) {
		logger.info("Start {} with {} {}", stage, fxmlLoader, dnsEvents);

		logger.info("Connect to WS server");
		WebSocketClient client = new WebSocketClientBuilder("ws://10.0.0.10:8080/chat/javafx-client")
			.simpleListener(dnsEvents)
			.build();

		
		
		try (InputStream is = ClientGui.class.getResourceAsStream("ClientGui.fxml")) {
			logger.info("Load FXML");
			Parent root = (Parent) fxmlLoader.load(is);	
			stage.setScene(new Scene(root, 640, 480));
			logger.info("show");
			stage.show();
			
			logger.info("EXIT");

		} catch (IOException e) {
			throw new IllegalStateException("cannot load FXML login screen", e);
		}
		
		logger.info("Bye bye!");

	}
	
}
