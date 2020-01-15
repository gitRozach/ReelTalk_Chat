package gui.client.components.contextMenu;

import com.jfoenix.controls.JFXButton;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

//
public class MenuItemButton extends JFXButton {
	public MenuItemButton(String text) {
		super(text);
		setButtonType(ButtonType.FLAT);
		setRipplerFill(Color.DEEPSKYBLUE);
		setFont(Font.font("Tahoma", 20d));
	}

	public MenuItemButton(String text, Node graphic) {
		super(text, graphic);
		setFont(Font.font("Tahoma", 20d));
	}

	public void setFixedWidth(double width) {
		this.setPrefWidth(width);
		this.setMinWidth(width);
		this.setMaxWidth(width);
	}

	public void setFixedHeight(double height) {
		this.setPrefHeight(height);
		this.setMinHeight(height);
		this.setMaxHeight(height);
	}
}