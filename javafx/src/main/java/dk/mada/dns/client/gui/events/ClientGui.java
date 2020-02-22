package dk.mada.dns.client.gui.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


public class ClientGui {
	private static final Logger logger = LoggerFactory.getLogger(ClientGui.class);
    
    class Foo  {}
    
    @FXML private AnchorPane root;
    @FXML private TextField path;
    @FXML private TableView<Foo> list;
    @FXML private TableColumn<Foo, String> colDomain;
    @FXML private TableColumn<Foo, String> colIp;
    @FXML private TableColumn<Foo, String> colClient;
    @FXML private Label status;
    @FXML private Button clear;

    private ObservableList<Foo> modelList;
    
    @FXML
    public void initialize() {
        logger.info("init {}", root);
    }
    
    @FXML
    private void onClear() {
    	
    }

}
