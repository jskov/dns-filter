package dk.mada.dns.client;

import java.util.concurrent.CountDownLatch;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.stage.Stage;

// after https://dzone.com/articles/fxml-javafx-powered-cdi-jboss
public class JavaCdiFxTest extends Application {
	private static final Logger logger = LoggerFactory.getLogger(JavaCdiFxTest.class);
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		
	   Weld weld = new Weld();
	   try (WeldContainer container = weld.initialize()) {
		   CountDownLatch guiClosing = container.select(Starter.class).get().start(primaryStage);
		   logger.info("Waiting for GUI to close...");
		   guiClosing.await();
		   logger.info("GUI done, quitting!");
	   }
	}
	
    public static void main(String[] args) {
    	Application.launch(args);
    }
}