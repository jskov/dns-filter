package dk.mada.dns.web.app;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.listbox.ListBox;

public class PrefListComponent {
	private static final Logger logger = LoggerFactory.getLogger(PrefListComponent.class);
	private final Supplier<Collection<String>> contentSupplier;
	private final ListBox<String> list;
	
	public PrefListComponent(Supplier<Collection<String>> contentSupplier) {
		this.contentSupplier = contentSupplier;
		
		list = new ListBox<>();
	}

	
	private void makeComponent() {
		
		Div main = new Div();

	}
	
	private void update() {
		logger.info("Updating list of whitelisted hosts");
		List<String> items = contentSupplier.get().stream()
				.sorted()
				.collect(toList());
		list.setItems(items);
	}

}
