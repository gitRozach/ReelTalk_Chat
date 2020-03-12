package gui.components.messageField.items;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ParagraphMessageItem extends VBox implements MessageFieldItem  {
	protected HBox spacer;
	
	public ParagraphMessageItem() {
		this(30d);
	}
	
	public ParagraphMessageItem(double height) {
		initSpacer(height);
		initStyleClass("paragraph-message-item");
	}
	
	private void initSpacer(double height) {
		Label label = new Label("");
		spacer = new HBox(label);
		spacer.setStyle("-fx-border-color: red;");
		//GUITools.setFixedHeightOf(spacer, height);
		//ParagraphMessageItem.setHgrow(spacer, Priority.ALWAYS);
		setFillWidth(true);
		HBox.setHgrow(label, Priority.ALWAYS);
		getChildren().add(spacer);
	}
	
	private void initStyleClass(String styleClass) {
		getStyleClass().add(styleClass);
	}
	
	@Override
	public String toMessageString() {
		return System.lineSeparator();
	}
}
