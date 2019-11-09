package dk.mada.dns.web.app;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route
@PWA(name = "My UI in Java", shortName = "Hello world")
public class MainView extends VerticalLayout {
  public MainView() {
    TextField name = new TextField("Name");
    Paragraph greeting = new Paragraph("");

    Button button = new Button("Greet", event -> {
      greeting.setText("Hello " + name.getValue());
    });

    add(name, button, greeting);
  }
}
