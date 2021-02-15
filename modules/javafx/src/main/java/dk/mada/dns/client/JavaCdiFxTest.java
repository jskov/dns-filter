package dk.mada.dns.client;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.stage.Stage;

// after https://dzone.com/articles/fxml-javafx-powered-cdi-jboss
public class JavaCdiFxTest extends Application {
	private static final Logger logger = LoggerFactory.getLogger(JavaCdiFxTest.class);
	
	private WeldContainer container;
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
	   Weld weld = new Weld();
	   container = weld.initialize();
	   container.select(Starter.class).get().start(primaryStage);
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		// FIXME: must close websocket client
		
		if (container != null) {
			container.close();
		}
		
		// exit
		System.exit(0);
	}
	
	
    public static void main(String[] args) {
    	Application.launch(args);
    }
}