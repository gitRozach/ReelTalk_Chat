package gui.client.components.messageField;

import gui.client.components.messageField.messageFieldItems.EmojiMessageItem;
import gui.client.components.messageField.messageFieldItems.MessageFieldItem;
import gui.client.components.messageField.messageFieldItems.ParagraphMessageItem;
import gui.client.components.messageField.messageFieldItems.WordMessageItem;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class EmojiTextField extends ScrollPane {
	private IntegerProperty oldCaretPositionProperty;
	private IntegerProperty currentIndexProperty;
	
	private FlowPane inputFlowPane;
	private HBox inputBox;
	private TextField inputField;
	
	private EventHandler<KeyEvent> onEnterPressed;
	
	public EmojiTextField() {
		initProperties();
		initInputField("");
		initInputBox();
		initInputFlowPane();
		initRoot();
		initEventHandlers();
		initStyleClass();
	}
	
	private void initStyleClass() {
		getStyleClass().add("smiley-text-field");
		inputFlowPane.getStyleClass().add("smiley-flow-pane");
	}
	
	private void initProperties() {
		oldCaretPositionProperty = new SimpleIntegerProperty(0);
		
		currentIndexProperty = new SimpleIntegerProperty(0);
		currentIndexProperty.addListener((obs, oldV, newV) -> {
			System.out.println("New Index: " + newV.intValue());
		});
		
	}
	
	private void initInputField(String initText) {
		inputField = new TextField(initText);
		inputField.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/OpenSansEmoji.ttf"), 15f));
		inputField.caretPositionProperty().addListener((obs, oldV, newV) -> {
			setOldCaretPosition(oldV.intValue());
		});
		inputField.setPrefWidth(initText.length() == 0 ? 10d : initText.length() * 15d);
		inputField.textProperty().addListener((obs, oldV, newV) -> {
			  Platform.runLater(() -> {
			        Text text = new Text(newV);
			        text.setFont(inputField.getFont());
			        double width = text.getLayoutBounds().getWidth()
			                + inputField.getPadding().getLeft() + inputField.getPadding().getRight()
			                + 5d; // Add some spacing
			        inputField.setPrefWidth(width);
			        inputField.positionCaret(inputField.getCaretPosition()); // If you remove this line, it flashes a little bit
			    });
		});
	}
	
	private void initInputBox() {
		inputBox = new HBox(inputField);
		inputBox.getStyleClass().add("input-box");
		inputBox.setAlignment(Pos.CENTER_LEFT);
		inputBox.setSpacing(0d);
	}
	
	private void initInputFlowPane() {
		inputFlowPane = new FlowPane(6d, 8d);
		inputFlowPane.setRowValignment(VPos.CENTER);
		inputFlowPane.setAlignment(Pos.TOP_LEFT);
		inputFlowPane.getChildren().add(inputBox);
	}
	
	private void initRoot() {
		setContent(inputFlowPane);
		setFitToWidth(true);
		setFitToWidth(true);
	}
	
	private void initEventHandlers() {
		inputField.addEventFilter(KeyEvent.ANY, event -> {				
			if(event.getEventType() == KeyEvent.KEY_PRESSED) {
				if(event.getCode() == KeyCode.SPACE)
					onSpaceKeyPressed(event);
				else if(event.getCode() == KeyCode.ENTER)
					onEnterPressed(event);
				else if(event.getCode() == KeyCode.BACK_SPACE)
					onBackSpacePressed(event);
				else if(event.getCode() == KeyCode.LEFT)
					onLeftPressed(event);
				else if(event.getCode() == KeyCode.RIGHT)
					onRightPressed(event);
			}
			if(event.getEventType() == KeyEvent.KEY_TYPED) {
				if(event.getCharacter().equals(" "))
					event.consume();
			}
		});
		
		onEnterPressed = keyEvent -> {};
	}
	
	private void onSpaceKeyPressed(KeyEvent event) {
		event.consume();
		String wordText = inputField.getText().substring(0, getCaretPosition());
		String leftInput = inputField.getText().substring(getCaretPosition());
		
		if(wordText.trim().length() == 0)
			return;
		inputField.setText(leftInput);
		addText(wordText);
	}
	
	private void onBackSpacePressed(KeyEvent event) {
		if(getCaretPosition() == 0 && getCurrentIndex() > 0) {
			String newInputFieldText = null;
			int newCaretPos = -1;
			Node itemToRemove = inputFlowPane.getChildren().get(getCurrentIndex() - 1);
			
			if(itemToRemove instanceof ParagraphMessageItem) {
				newInputFieldText = "";
				newCaretPos = 0;
			}
			else if(itemToRemove instanceof EmojiMessageItem) {
				newInputFieldText = "";
				newCaretPos = 0;
			}
			else if(itemToRemove instanceof WordMessageItem) {
				WordMessageItem word = (WordMessageItem) itemToRemove;
				newInputFieldText = word.getWord().concat(event.getCode() == KeyCode.LEFT ? " " + inputField.getText() : inputField.getText());
				newCaretPos = word.getWord().length();
			}
			if(removeItem(getCurrentIndex() - 1) != null) {
				inputField.setText(newInputFieldText);
				setCaretPosition(newCaretPos);
			}
			event.consume();
		}
	}
	
	private void onLeftPressed(KeyEvent event) {
		if(getCaretPosition() == 0 && getCurrentIndex() >= 0) {
			String newInputFieldText = null;
			MessageFieldItem previousItem = getCurrentIndex() <= 0 ? null : (MessageFieldItem) inputFlowPane.getChildren().get(getCurrentIndex() - 1);
			int previousIndex = getCurrentIndex() - 1;
			boolean hasSmileyRight = hasCurrentSelectionSmileyRightSide();
			boolean hasText = hasCurrentSelectionText();
			boolean hasSmileyLeft = hasCurrentSelectionSmileyLeftSide();
			
			if(hasSmileyRight)
				addItem(getCurrentIndex() + 1, getCurrentSelectionSmileyRightSide());
			if(hasText)
				addItem(getCurrentIndex() + 1, new WordMessageItem(getCurrentText()));
			if(hasSmileyLeft) {
				addSmileyToCurrentSelectionRightSide(new EmojiMessageItem(getCurrentSelectionSmileyLeftSide().getFilePath()));
				removeSmileyFromCurrentSelectionLeftSide();
				return;
			}
			clearInputEmojis();
			
			if(previousItem instanceof ParagraphMessageItem) {
				newInputFieldText = "";
			}
			else if(previousItem instanceof EmojiMessageItem) {
				EmojiMessageItem smiley = (EmojiMessageItem)previousItem;
				newInputFieldText = "";
				addSmileyToCurrentSelectionLeftSide(new EmojiMessageItem(smiley.getFilePath()));
			}
			else if(previousItem instanceof WordMessageItem) {
				WordMessageItem word = (WordMessageItem) previousItem;
				newInputFieldText = word.getWord();
			}
			if(removeItem(previousIndex) != null) {
				inputField.setText(newInputFieldText);
				setCaretPosition(newInputFieldText.length());
			}
			event.consume();
		}
	}
	
	private void onRightPressed(KeyEvent event) {
		if(getCaretPosition() == getCurrentText().length() && getCurrentIndex() < length()) {
			String newInputFieldText = null;
			Node nextItem = getCurrentIndex() >= length() - 1 ? null : inputFlowPane.getChildren().get(getCurrentIndex() + 1);
			
			if(hasCurrentSelectionSmileyRightSide()) {
				addSmileyToCurrentSelectionLeftSide(new EmojiMessageItem(getCurrentSelectionSmileyRightSide().getFilePath()));
				removeSmileyFromCurrentSelectionRightSide();
				return;
			}
			if(hasCurrentSelectionText())
				addItem(getCurrentIndex(), new WordMessageItem(getCurrentText()));
			if(hasCurrentSelectionSmileyLeftSide())
				addItem(getCurrentIndex(), getCurrentSelectionSmileyLeftSide());
			clearInputEmojis();
			
			if(nextItem instanceof ParagraphMessageItem) {
				newInputFieldText = "";
			}
			else if(nextItem instanceof EmojiMessageItem) {
				EmojiMessageItem smiley = (EmojiMessageItem) nextItem;
				newInputFieldText = "";
				addSmileyToCurrentSelectionRightSide(new EmojiMessageItem(smiley.getFilePath()));
			}
			else if(nextItem instanceof WordMessageItem) {
				WordMessageItem word = (WordMessageItem) nextItem;
				newInputFieldText = word.getWord();
			}
			if(removeItem(nextItem)) {
				inputField.setText(newInputFieldText);
				setCaretPosition(0);
			}
			event.consume();
		}
	}
	
	private void setTextFieldContent(MessageFieldItem item, int caretPos) {
		String newInputFieldText = null;			
		clearInput();
		
		if(item == null)
			return;
		
		if(item instanceof ParagraphMessageItem) {
			newInputFieldText = "";
		}
		else if(item instanceof EmojiMessageItem) {
			EmojiMessageItem smiley = (EmojiMessageItem)item;
			newInputFieldText = "";
			if(caretPos == 0)
				addSmileyToCurrentSelectionLeftSide(smiley);
			else
				addSmileyToCurrentSelectionRightSide(smiley);
		}
		else if(item instanceof WordMessageItem) {
			WordMessageItem word = (WordMessageItem) item;
			newInputFieldText = word.getWord();
		}
		inputField.setText(newInputFieldText);
	}
	
	private void onEnterPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			if(event.isShiftDown())
				addItem(new ParagraphMessageItem());
			else
				onEnterPressed.handle(event);
		}
	}
	
	private void clearInputText() {
		inputField.clear();
	}
	
	private void clearInputEmojis() {
		removeSmileyFromCurrentSelectionLeftSide();
		removeSmileyFromCurrentSelectionRightSide();
	}
	
	private void clearInput() {
		clearInputEmojis();
		clearInputText();
	}
	
	private boolean removeSmileyFromCurrentSelectionLeftSide() {
		return hasCurrentSelectionSmileyLeftSide() && inputBox.getChildren().remove(0) != null;
	}
	
	private boolean removeSmileyFromCurrentSelectionRightSide() {
		return hasCurrentSelectionSmileyRightSide() && inputBox.getChildren().remove(inputBox.getChildren().size() - 1) != null;
	}
	
	private boolean hasCurrentSelectionText() {
		return !inputField.getText().isEmpty();
	}
	
	private boolean hasCurrentSelectionSmileyLeftSide() {
		return inputBox.getChildren().indexOf(inputField) == 1;		
	}
		
	private boolean hasCurrentSelectionSmileyRightSide() {
		return inputBox.getChildren().size() - 1 > inputBox.getChildren().indexOf(inputField);
	}
	
	private EmojiMessageItem getCurrentSelectionSmileyLeftSide() {
		if(!hasCurrentSelectionSmileyLeftSide())
			return null;
		return (EmojiMessageItem) inputBox.getChildren().get(0);
	}
	
	private EmojiMessageItem getCurrentSelectionSmileyRightSide() {
		if(!hasCurrentSelectionSmileyRightSide())
			return null;
		return (EmojiMessageItem) inputBox.getChildren().get(inputBox.getChildren().size() > 2 ? 2 : 1);
	}
	
	private void addSmileyToCurrentSelectionLeftSide(EmojiMessageItem message) {
		if(message == null)
			return;
		removeSmileyFromCurrentSelectionLeftSide();
		inputBox.getChildren().add(0, message);
	}
	
	private void addSmileyToCurrentSelectionRightSide(EmojiMessageItem message) {
		if(message == null)
			return;
		removeSmileyFromCurrentSelectionRightSide();
		inputBox.getChildren().add(inputBox.getChildren().size(), message);
	}
	
	private void incrementCurrentIndex() {
		setCurrentIndex(getCurrentIndex() + 1);
	}
	
	private void decrementCurrentIndex() {
		setCurrentIndex(getCurrentIndex() - 1);
	}
	
	private boolean addInputBox(int index, MessageFieldItem content, int caretPos) {
		setTextFieldContent(content, caretPos);
		if(inputFlowPane.getChildren().contains(inputBox))
			inputFlowPane.getChildren().remove(inputBox);
		inputFlowPane.getChildren().add(index, inputBox);
		setCurrentIndex(index);
		return true;
	}
	
	private boolean moveInputBox(int index) {
		Node selectedItem = getItem(index);
		if(selectedItem == null)
			return false;
		if(selectedItem instanceof HBox)
			return false;
		else if(selectedItem instanceof WordMessageItem) {
			WordMessageItem content = (WordMessageItem)selectedItem;
			//addText(getCurrentText());
			addCurrentInput();
			setTextFieldContent(content, 0);
		}
		else if(selectedItem instanceof EmojiMessageItem) {

		}
		else if(selectedItem instanceof ParagraphMessageItem) {
			
		}
		if(inputFlowPane.getChildren().contains(inputBox))
			inputFlowPane.getChildren().remove(inputBox);
		inputFlowPane.getChildren().remove(index);
		inputFlowPane.getChildren().add(index, inputBox);
		inputField.requestFocus();
		setCurrentIndex(index);	
		return true;
	}
	
	public Node moveCaret(int itemIndex, int caretPos) {
		Node selectedItem = getItem(itemIndex);
		if(selectedItem instanceof HBox) {
			
		}
		else if(selectedItem instanceof MessageFieldItem) {
			moveInputBox(itemIndex);
		}
		return selectedItem;
	}
	
	public int addText(String text) {
		String[] words = text.split(" ");
		for(String currentWord : words) {
			if(!currentWord.isEmpty()) {
				WordMessageItem wordMessage = new WordMessageItem(currentWord);
				addItem(wordMessage);
			}
		}
		return words.length;
	}
	
	public int addCurrentInput() {
		int addedItems = 0;
		if(hasCurrentSelectionSmileyLeftSide()) {
			addItem(new EmojiMessageItem(getCurrentSelectionSmileyLeftSide().getFilePath()));
			++addedItems;
		}
		if(hasCurrentSelectionText())
			addedItems += addText(getCurrentText());
		if(hasCurrentSelectionSmileyRightSide()) {
			addItem(new EmojiMessageItem(getCurrentSelectionSmileyRightSide().getFilePath()));
			++addedItems;
		}
		return addedItems;
	}
	
	public void appendItem(MessageFieldItem newItem) {
		addItem(inputFlowPane.getChildren().size(), newItem);
	}
	
	public void addItem(MessageFieldItem newItem) {
		addItem(getCurrentIndex(), newItem);
	}
	
	public void addItem(int index, MessageFieldItem newItem) {
		if(newItem == null)
			return;
		boolean added = false;
		
		if(newItem instanceof ParagraphMessageItem) {
			ParagraphMessageItem paragraph = (ParagraphMessageItem)newItem;
			paragraph.getStyleClass().add("paragraph-message-item");
			inputFlowPane.getChildren().add(index, paragraph);
			added = true;
		} 			
		else if(newItem instanceof EmojiMessageItem) {
			EmojiMessageItem smiley = (EmojiMessageItem)newItem;
			smiley.getStyleClass().add("smiley-message-item");
			inputFlowPane.getChildren().add(index, smiley);	
			added = true;
		}
		else if(newItem instanceof WordMessageItem) {
			WordMessageItem word = (WordMessageItem)newItem;
			word.setOnMouseClicked(a -> moveCaret(inputFlowPane.getChildren().indexOf(word), word.getText().length()));
			word.getStyleClass().add("word-message-item");
			inputFlowPane.getChildren().add(index, word);
			added = true;
		}
		if(added) {
			clearInput();
			if(index <= getCurrentIndex()) {
				incrementCurrentIndex();
				if(getCurrentIndex() < length() - 1)
					addInputBox(getCurrentIndex() + 1, (MessageFieldItem)getItem(getCurrentIndex() + 1), 0);
			}
		}
	}
	
	public boolean removeItem(Node item) {
		return removeItem(inputFlowPane.getChildren().indexOf(item)) != null;
	}
	
	public Node removeItem(int index) {
		if(index < 0 || index >= inputFlowPane.getChildren().size())
			return null;
		Node item = null;
		
		if(index == getCurrentIndex()) {
			clearInput();
			return inputBox;
		}
		else {
			item = inputFlowPane.getChildren().remove(index);
			if(index < getCurrentIndex())
				decrementCurrentIndex();
		}
		return item;
	}
	
	public String getText() {
		StringBuilder builder = new StringBuilder();
		for(Node currentNode : inputFlowPane.getChildren()) {
			if(currentNode instanceof MessageFieldItem)
				builder.append(((MessageFieldItem)currentNode).toMessageString() + " ");
			else if(currentNode instanceof HBox) {
				System.out.println("has text? : " + hasCurrentSelectionText());
				System.out.println(getCurrentText());
				
				if(hasCurrentSelectionSmileyLeftSide())
					builder.append(getCurrentSelectionSmileyLeftSide().toMessageString() + " ");
				if(hasCurrentSelectionText())
					builder.append(getCurrentText() + " ");
				if(hasCurrentSelectionSmileyRightSide())
					builder.append(getCurrentSelectionSmileyRightSide().toMessageString() + " ");
			}
		}
		return builder.toString().trim();
	}
	
	public int clear() {
		int removedItems = inputFlowPane.getChildren().size();
		inputField.clear();
		inputFlowPane.getChildren().clear();
		inputFlowPane.getChildren().add(inputBox);
		inputField.requestFocus();
		setCurrentIndex(0);
		return removedItems;
	}
	
	public int length() {
		return inputFlowPane.getChildren().size();
	}
	
	public boolean isEmpty() {
		return inputFlowPane.getChildren().size() > 1;
	}
	
	public String getCurrentText() {
		return inputField.getText();
	}

	public Node getItem(int index) {
		if(index < 0 || index >= inputFlowPane.getChildren().size())
			return null;
		return inputFlowPane.getChildren().get(index);
	}
	
	public IntegerProperty oldCaretPositionProperty() {
		return oldCaretPositionProperty;
	}

	public int getOldCaretPosition() {
		return oldCaretPositionProperty.get();
	}

	private void setOldCaretPosition(int value) {
		oldCaretPositionProperty.set(value);
	}
	
	public int getCaretPosition() {
		return inputField.getCaretPosition();
	}
	
	public void setCaretPosition(int pos) {
		inputField.requestFocus();
		inputField.positionCaret(pos);
	}
	
	public ReadOnlyIntegerProperty currentIndexProperty() {
		return currentIndexProperty;
	}
	
	public int getCurrentIndex() {
		return currentIndexProperty.get();
	}
	
	public void setCurrentIndex(int index) {
		currentIndexProperty.set(index);
	}
	
	public TextField getInputField() {
		return inputField;
	}
	
	public void setOnEnterPressed(EventHandler<KeyEvent> handler) {
		if(handler == null)
			onEnterPressed = event -> {};
		else
			onEnterPressed = handler;
	}
}
