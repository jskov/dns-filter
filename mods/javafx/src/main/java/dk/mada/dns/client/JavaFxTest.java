package dk.mada.dns.client;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class JavaFxTest extends Application {
    @Override
    public void start(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(new StackPane(new Label("Hello World!")),
                bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
    	JavaFxTest.launch(args);
	}
}