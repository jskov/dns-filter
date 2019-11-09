package dk.mada.dns.web.app;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "")
public class MainView extends Composite<Div> {
	public MainView() {
		getContent().add(new Span("Here we are"));
	}
}
