package dk.mada.dns.web.app;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import dk.mada.dns.config.BlockedItem;

/**
 * Shows a preference list of BlockedItems. Allows deletion from and addition to list.
 * 
 * Layout is atrocious, and ListBox with its (unused) checkmark is a bad choice.
 * But it'll do for a bit.
 */
public class PrefListComponent {
	private static final Logger logger = LoggerFactory.getLogger(PrefListComponent.class);
	private final Supplier<Collection<? extends BlockedItem>> contentSupplier;
	private final ListBox<BlockedItem> list;
	private String title;
	private Consumer<String> deleteItem;
	private BiConsumer<String, String> addItem;
	private Div mainComp;

	public PrefListComponent(String title, Supplier<Collection<? extends BlockedItem>> contentSupplier) {
		this.title = null;
		this.contentSupplier = null;
		this.list = null;
	}
	
	public PrefListComponent(String title, Supplier<Collection<? extends BlockedItem>> contentSupplier, Consumer<String> deleteItem, BiConsumer<String, String> addItem) {
		this.title = title;
		this.contentSupplier = contentSupplier;
		this.deleteItem = deleteItem;
		this.addItem = addItem;
		
		list = new ListBox<>();
		list.setRenderer(makeItemRenderer());

		mainComp = new Div(new H3(title), list, createInputFields());
	}

	public Div getComponent() {
		update();
		return mainComp;
	}
	
	private Div createInputFields() {
		TextField nameComp = new TextField("Name:");
		nameComp.setClearButtonVisible(true);
		
		TextField reasonComp = new TextField("Reason:");
		reasonComp.setClearButtonVisible(true);

		Button addButton = new Button(new Icon(VaadinIcon.PLUS), event -> {
			String name = nameComp.getValue();
			String reason = reasonComp.getValue();
			logger.info("Add {} to {} because {}", name, title, reason);
			
			nameComp.clear();
			reasonComp.clear();
			addItem.accept(name, reason);
		});
		addButton.setEnabled(false);

		ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> onInputChange = vc -> {
			boolean inputValid = !(nameComp.isEmpty() || reasonComp.isEmpty());
			addButton.setEnabled(inputValid);
		};
		
		nameComp.addValueChangeListener(onInputChange);
		reasonComp.addValueChangeListener(onInputChange);

		return new Div(nameComp, reasonComp, addButton);
	}
	
	public void update() {
		logger.info("Updating list of {}", title);
		List<BlockedItem> items = contentSupplier.get().stream()
				.sorted()
				.collect(toList());
		list.setItems(items);
	}

	private ComponentRenderer<Div, BlockedItem> makeItemRenderer() {
		return new ComponentRenderer<>(item -> {
		    String name = item.getName();
			Label nameLabel = new Label(name);
	
		    Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
		    	logger.info("Delete {} from {}", nameLabel, title);
		    	deleteItem.accept(name);
		    	update();
		    });
	
		    Div labels = new Div(nameLabel);
		    Div layout = new Div(labels, button);
	
		    labels.getStyle().set("display", "flex")
		            .set("flexDirection", "column")
		    		.set("marginRight", "40px");
		    layout.getStyle().set("display", "flex")
		            .set("alignItems", "right");
	
		    return layout;
		});
	}

}
