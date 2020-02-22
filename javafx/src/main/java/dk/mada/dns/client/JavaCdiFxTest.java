package dk.mada.dns.client;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javafx.application.Application;
import javafx.stage.Stage;

// after https://dzone.com/articles/fxml-javafx-powered-cdi-jboss
public class JavaCdiFxTest extends Application {
	@Override
	public void start(final Stage primaryStage) throws Exception {
		
	   Weld weld = new Weld();
	   try (WeldContainer container = weld.initialize()) {
		   container.select(Starter.class).get().start(primaryStage);
	   }
	}
	
    public static void main(String[] args) {
    	Application.launch(args);
    }
}