package gui.client.components.messageField;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;

public class EmojiTab extends Tab {
	private ScrollPane smileyScrollPane;
	private FlowPane smileyFlowPane;

	public EmojiTab() {
		smileyScrollPane = new ScrollPane();
		smileyScrollPane.getStyleClass().add("smiley-scroll-pane");
		smileyScrollPane.setFitToWidth(true);
		smileyScrollPane.setFitToHeight(true);

		smileyFlowPane = new FlowPane();
		smileyFlowPane.getStyleClass().add("smiley-flow-pane");
		smileyFlowPane.setPadding(new Insets(5));
		smileyFlowPane.setHgap(3);
		smileyFlowPane.setVgap(3);

		smileyScrollPane.setContent(smileyFlowPane);
		setClosable(false);
		setContent(smileyScrollPane);
	}

	public void addSmiley(Node smiley) {
		if (smiley != null)
			smileyFlowPane.getChildren().add(smiley);
	}

	public boolean setSmiley(int index, Node smiley) {
		if (smiley != null && index >= 0 && index < smileyFlowPane.getChildren().size()) {
			smileyFlowPane.getChildren().set(index, smiley);
			return true;
		} 
		else
			return false;
	}

	public boolean removeSmiley(int index) {
		if (smileyFlowPane.getChildren().size() > index) {
			smileyFlowPane.getChildren().remove(index);
			return true;
		} 
		else
			return false;
	}

	public void setSmileys(ObservableList<Node> smileys) {
		if (smileys != null)
			if (!smileys.isEmpty())
				smileyFlowPane.getChildren().setAll(smileys);
	}

	public ObservableList<Node> getSmileys() {
		return smileyFlowPane.getChildren();
	}
}
