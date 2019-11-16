package dk.mada.dns.web.app;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import dk.mada.dns.config.Configuration;

@Route(value = "")
@PWA(name = "My UI in Java", shortName = "Hello world")
public class MainView extends VerticalLayout {
	private static final Logger logger = LoggerFactory.getLogger(MainView.class);
	@Inject	private Configuration config;

	public MainView() {
		
		logger.info("At creation {}", config);
		
		
		
		TextField name = new TextField("Name");
		Paragraph greeting = new Paragraph("");

		Button button = new Button("Click me", event -> Notification.show("Jeps " + Objects.toString(config)));
		add(button);

		add(name, button, greeting);
	}
	
	@PostConstruct
	public void after() {
		
		TextField x = new TextField("Help");
		
		add(x);
		
		logger.info("After creation {}", config);
	}
}
