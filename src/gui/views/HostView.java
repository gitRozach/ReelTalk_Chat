package gui.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class HostView extends StackPane {
	private JFXTabPane tabPane;
	private Tab loggingTab;
	private VBox loggingBox;
	
	private JFXTextArea loggingField;
	private HBox loggingButtons;
	private JFXButton stopServerButton;
	
	public HostView() {
		initialize();
	}
	
	public void initialize() {
		initLoggingControls();
		initContainers();
		initRoot();
	}
	
	private void initContainers() {
		loggingBox = new VBox(20d);
		loggingBox.setFillWidth(true);
		loggingBox.setAlignment(Pos.TOP_CENTER);
		loggingBox.getChildren().addAll(loggingField, loggingButtons);
		VBox.setVgrow(loggingField, Priority.ALWAYS);
		
		loggingTab = new Tab("Logging", loggingBox);
		
		tabPane = new JFXTabPane();
		tabPane.getTabs().addAll(loggingTab);
	}
	
	private void initLoggingControls() {
		loggingField = new JFXTextArea();
		loggingField.setFont(Font.font(18d));
		
		stopServerButton = new JFXButton("Server stoppen");
		loggingButtons = new HBox(20d);
		loggingButtons.getChildren().addAll(stopServerButton);
	}
	
	private void initRoot() {
		setAlignment(Pos.TOP_LEFT);
		setPadding(new Insets(20d));
		getChildren().add(tabPane);
	}

}
