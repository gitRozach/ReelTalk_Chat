package gui.client.components.messageField.messageFieldItems;

import javafx.scene.control.Label;

public class WordMessageItem extends Label implements MessageFieldItem {	
	public WordMessageItem() {
		this("");
	}
	
	public WordMessageItem(String word) {
		initialize(word);
	}
	
	private void initialize(String initWord) {
		initText(initWord);
		initStyleClass("word-message-item");
	}
	
	private void initText(String word) {
		setText(word);
	}
	
	private void initStyleClass(String styleClass) {
		getStyleClass().add(styleClass);
	}
	
	@Override
	public String toMessageString() {
		return getText();
	}
	
	public int length() {
		return getText().length();
	}
	
	public String getWord() {
		return getText();
	}
	
	public void setWord(String newWord) {
		setText(newWord);
	}
}
