package main;


import java.io.IOException;

import org.controlsfx.control.CheckListView;
import org.controlsfx.control.InfoOverlay;

import com.jfoenix.controls.JFXButton;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class SSLTest extends Application{
	public HBox parent;
	
	ButtonBar bb;
	JFXButton button1;
	JFXButton button2;
	JFXButton button3;
	JFXButton button4;
	JFXButton button5;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override 
	public void start(Stage window) throws IOException {		
		bb = new ButtonBar();
		
		button1 = new JFXButton("B1");
		button1.setId("#b1");
		button2 = new JFXButton("B2");
		button2.setId("#b2");
		button3 = new JFXButton("B3");
		button3.setId("#b3");
		button4 = new JFXButton("B4");
		button4.setId("#b4");
		button5 = new JFXButton("B5");
		button5.setId("#b5");
		
		bb.getButtons().addAll(button1, button2, button3, button4, button5);

		ObservableList<String> itemList = FXCollections.observableArrayList("First", "Second", "Third");
		CheckListView<String> testView = new CheckListView<String>(itemList);
		
		InfoOverlay over = new InfoOverlay(testView, "This control was initially developed by my colleague David Grieve before being integrated into ControlsFX. It is designed to show a small blurb of text above a node (most commonly an ImageView, but it will work with any Node). The text can be collapsed down to a single line, or expanded to show the entire text. In some ways, it can be thought of as a always visible tooltip (although by default it is collapsed so only the first line is shown – hovering over it (or clicking on it if the showOnHover functionality is disabled) will expand it to show all text). Shown below is a screenshot of the InfoOverlay control in both its collapsed and expanded states:");
		
		parent = new HBox(bb);
		parent.setId("#parentBox");
		parent.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(parent, Priority.ALWAYS);
		parent.getChildren().addAll(testView, over);		
		
		window.setScene(new Scene(parent));
		window.setTitle("JFX Test application");
		window.show();
	}
}
