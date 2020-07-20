package dk.mada.dns.client;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.mada.dns.client.gui.events.ClientGui;
import dk.mada.dns.client.rest.WebsocketClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter {
	private static final Logger logger = LoggerFactory.getLogger(Starter.class);
	@Inject FXMLLoader fxmlLoader;
	
	@Inject WebsocketClient client;

	public void start(Stage stage) {
		logger.info("Start {} with {}", stage, fxmlLoader);

		stage.setOnCloseRequest(eh -> {
		    System.exit(0);
		});

		client.connect();

		try (InputStream is = ClientGui.class.getResourceAsStream("ClientGui.fxml")) {
			Parent root = (Parent) fxmlLoader.load(is);	
			stage.setScene(new Scene(root, 640, 480));
			stage.show();
		} catch (IOException e) {
			throw new IllegalStateException("cannot load FXML login screen", e);
		}
	}
}