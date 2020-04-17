package gui.components.contextMenu;

import com.jfoenix.controls.JFXButton;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MenuItemButton extends JFXButton {
	public MenuItemButton(String text) {
		super(text);
		initialize();
	}

	public MenuItemButton(String text, Node graphic) {
		super(text, graphic);
		initialize();
	}
	
	private void initialize() {
		setButtonType(ButtonType.RAISED);
		setRipplerFill(Color.GREEN);
		setFont(Font.font("Tahoma", 20d));
	}
}