package gui.components.messageField;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;

public class EmojiTab extends Tab {
	private ScrollPane emojiScrollPane;
	private FlowPane emojiFlowPane;

	public EmojiTab() {
		initialize();
	}
	
	private void initialize() {
		initEmojiFlowPane();
		initEmojiScrollPane();
		initRoot();
	}
	
	private void initEmojiFlowPane() {
		emojiFlowPane = new FlowPane();
		emojiFlowPane.getStyleClass().add("smiley-flow-pane");
		emojiFlowPane.setPadding(new Insets(5));
		emojiFlowPane.setHgap(3);
		emojiFlowPane.setVgap(3);
	}
	
	private void initEmojiScrollPane() {
		emojiScrollPane = new ScrollPane();
		emojiScrollPane.getStyleClass().add("smiley-scroll-pane");
		emojiScrollPane.setFitToWidth(true);
		emojiScrollPane.setFitToHeight(true);
		emojiScrollPane.setContent(emojiFlowPane);
	}
	
	private void initRoot() {
		setClosable(false);
		setContent(emojiScrollPane);
	}

	public void addEmoji(Node emoji) {
		if (emoji != null)
			emojiFlowPane.getChildren().add(emoji);
	}

	public boolean setEmoji(int index, Node emoji) {
		if (emoji != null && index >= 0 && index < emojiFlowPane.getChildren().size()) {
			emojiFlowPane.getChildren().set(index, emoji);
			return true;
		} 
		else
			return false;
	}

	public boolean removeEmojis(int index) {
		if (emojiFlowPane.getChildren().size() > index) {
			emojiFlowPane.getChildren().remove(index);
			return true;
		} 
		else
			return false;
	}

	public void setEmojis(ObservableList<Node> emojis) {
		if (emojis != null)
			if (!emojis.isEmpty())
				emojiFlowPane.getChildren().setAll(emojis);
	}
	
	public boolean hasEmojis() {
		return !getEmojis().isEmpty();
	}

	public ObservableList<Node> getEmojis() {
		return emojiFlowPane.getChildren();
	}
}
