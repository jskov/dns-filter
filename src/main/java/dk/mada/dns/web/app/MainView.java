package dk.mada.dns.web.app;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
		List<String> whitelistedNames = config.getWhitelistedHostNames().stream()
			.sorted()
			.collect(toList());
		
		
		ListBox<String> whitelistList = new ListBox<>();
		whitelistList.setItems(whitelistedNames);
		
		
		whitelistList.setRenderer(makeRenderer());		
		add(new H3("Whitelisted hosts"), whitelistList);
	
		
		
		
		logger.info("After creation {}", config);
	}

	private ComponentRenderer<Div, String> makeRenderer() {

		return new ComponentRenderer<>(item -> {
		    Label name = new Label(item);
	
		    Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
		    	logger.info("Delete {}", item);
//		        whitelistListBox.getDataProvider().refreshItem(item);
		    });
	
		    Div labels = new Div(name);
		    Div layout = new Div(labels, button);
	
		    labels.getStyle().set("display", "flex")
		            .set("flexDirection", "column")
		    		.set("marginRight", "10px");
		    layout.getStyle().set("display", "flex")
		            .set("alignItems", "right");
	
		    return layout;
		});
	}
}
