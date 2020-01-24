package gui.client.components.messageField;

import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.JFXFillTransition;

import gui.animations.Animations;
import gui.client.components.messageField.messageFieldItems.MessageFieldItem;
import gui.client.components.messageField.messageFieldItems.ParagraphMessageItem;
import gui.client.components.messageField.messageFieldItems.SmileyMessageItem;
import gui.client.components.messageField.messageFieldItems.WordMessageItem;
import gui.tools.GUITools;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import network.ssl.client.utils.CUtils;

//
public class MessageField extends VBox {
	// Components Container
	private HBox messageBox;

	// Main Components
	private SmileyTabPane smileyPane;
	private SmileyTextField inputField;
	private ImageView smileyButton;
	private ImageView fileButton;

	// Attributes
	private BooleanProperty openProperty;

	// EventHandler
	private EventHandler<? super KeyEvent> onEnterPressed;
	private EventHandler<? super MouseEvent> onSmileyButtonClicked;
	private EventHandler<? super MouseEvent> onFileButtonClicked;
	// Animations
	private Animation smileyIn;
	private Animation smileyOut;

	public enum SmileyCategory {
		A, B, C, D, E, F, G, H, I;

		public static SmileyCategory getByInt(int value) {
			switch (value) {
			case 0:
				return A;
			case 1:
				return B;
			case 2:
				return C;
			case 3:
				return D;
			case 4:
				return E;
			case 5:
				return F;
			case 6:
				return G;
			case 7:
				return H;
			case 8:
				return I;
			default:
				return null;
			}
		}
	}

	public enum SmileySkinColor {
		YELLOW, LIGHT_WHITE, WHITE, DEEP_WHITE, LIGHT_BLACK, BLACK;

		public static SmileySkinColor getByInt(int value) {
			switch (value) {
			case 0:
				return YELLOW;
			case 1:
				return LIGHT_WHITE;
			case 2:
				return WHITE;
			case 3:
				return DEEP_WHITE;
			case 4:
				return LIGHT_BLACK;
			case 5:
				return BLACK;
			default:
				return null;
			}
		}
	}

	public MessageField() {
		getStylesheets().add("/stylesheets/client/MessageField.css");

		messageBox = new HBox();
		messageBox.setFillHeight(true);
		messageBox.getStyleClass().add("message-box");
		messageBox.setOnDragOver(getOnDragOver());
		messageBox.setOnDragDropped(getOnDragDropped());
		messageBox.setPadding(new Insets(5d, 10d, 5d, 5d));

		openProperty = new SimpleBooleanProperty(false);

		smileyPane = new SmileyTabPane();
		smileyPane.setMinHeight(0d);
		smileyPane.setPrefHeight(0d);
		smileyPane.setMaxHeight(0d);
		smileyPane.setOpacity(0d);
		smileyPane.setOnKeyPressed(a -> onEnterPressed.handle(a));

		onEnterPressed = (keyEvent -> {});
		onSmileyButtonClicked = (mouseEvent -> {
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {
				if (isOpen())
					closeSmileyPane();
				else
					openSmileyPane();
			}
		});
		onFileButtonClicked = (mouseEvent -> {
		});
		
		inputField = new SmileyTextField();
		GUITools.setFixedHeightOf(inputField, 50d);
		HBox.setHgrow(inputField, Priority.ALWAYS);

		smileyButton = new ImageView(new Image("/resources/icons/img_smiley.png"));
		smileyButton.setSmooth(true);
		smileyButton.getStyleClass().add("smiley-button");
		smileyButton.setPickOnBounds(true);
		smileyButton.setOnMouseClicked(a -> onSmileyButtonClicked.handle(a));

		JFXNodesList fileButtons = new JFXNodesList();
		fileButtons.setRotate(90d);
		fileButtons.setSpacing(10d);
		
		fileButton = new ImageView(new Image("/resources/icons/img_file.png"));
		fileButton.setSmooth(true);
		fileButton.getStyleClass().add("file-button");
		fileButton.setPickOnBounds(true);
		fileButton.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		ImageView fileButton1 = new ImageView(new Image("/resources/icons/img_file.png"));
		fileButton1.setSmooth(true);
		fileButton1.getStyleClass().add("file-button");
		fileButton1.setPickOnBounds(true);
		fileButton1.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		ImageView fileButton2 = new ImageView(new Image("/resources/icons/img_file.png"));
		fileButton2.setSmooth(true);
		fileButton2.getStyleClass().add("file-button");
		fileButton2.setPickOnBounds(true);
		fileButton2.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		fileButtons.addAnimatedNode(new VBox(fileButton));
		fileButtons.addAnimatedNode(new VBox(fileButton1));
		fileButtons.addAnimatedNode(new VBox(fileButton2));

		Animation resizeInAnimation = Animations.newResizeAnimation(smileyPane, Duration.seconds(0.4d), false, 0d, 200d,
				Interpolator.EASE_BOTH);
		Animation fadeInAnimation = Animations.newFadeAnimation(smileyPane, Duration.seconds(0.5d), 0d, 1d,
				Interpolator.EASE_BOTH);

		Animation resizeOutAnimation = Animations.newResizeAnimation(smileyPane, Duration.seconds(0.3d), false, 200d,
				0d, Interpolator.EASE_BOTH);
		Animation fadeOutAnimation = Animations.newFadeAnimation(smileyPane, Duration.seconds(0.4d), 1d, 0d,
				Interpolator.EASE_BOTH);

		smileyIn = Animations.newParallelTransition(Interpolator.EASE_BOTH, 1, false, resizeInAnimation,
				fadeInAnimation);
		smileyOut = Animations.newParallelTransition(Interpolator.EASE_BOTH, 1, false, resizeOutAnimation,
				fadeOutAnimation);

		messageBox.getChildren().addAll(smileyButton, inputField, fileButtons);

		getChildren().addAll(messageBox, smileyPane);
	}

	public void openSmileyPane() {
		if (!isOpen() && smileyIn != null && smileyOut.getStatus() != Status.RUNNING) {
			setOpen(true);
			smileyIn.playFromStart();
		}
	}

	public void closeSmileyPane() {
		if (isOpen() && smileyOut != null && smileyIn.getStatus() != Status.RUNNING) {
			setOpen(false);
			smileyOut.playFromStart();
		}
	}
	
	public String getText() {
		return inputField.getText();
	}

	public BooleanProperty openProperty() {
		return openProperty;
	}

	public boolean isOpen() {
		return openProperty.get();
	}

	private void setOpen(boolean value) {
		openProperty.set(value);
	}

	public SmileyTabPane getSmileyPane() {
		return smileyPane;
	}

	public SmileyTextField getInputField() {
		return inputField;
	}

	public EventHandler<? super KeyEvent> getOnEnterPressed() {
		return onEnterPressed;
	}

	public void setOnEnterPressed(EventHandler<? super KeyEvent> ke) {
		onEnterPressed = ke;
	}

	public EventHandler<? super MouseEvent> getOnSmileyButtonClicked() {
		return onSmileyButtonClicked;
	}

	public void setOnSmileyButtonClicked(EventHandler<? super MouseEvent> me) {
		onSmileyButtonClicked = me;
	}

	public EventHandler<? super MouseEvent> getOnFileButtonClicked() {
		return onFileButtonClicked;
	}

	public void setOnFileButtonClicked(EventHandler<? super MouseEvent> me) {
		onFileButtonClicked = me;
	}
	
	/*
	 * 
	 */
	
	public class SmileyTextField extends ScrollPane{
		private IntegerProperty oldCaretPositionProperty;
		private IntegerProperty currentIndexProperty;
		
		private FlowPane inputFlowPane;
		private HBox inputBox;
		private TextField inputField;
		
		public SmileyTextField() {
			initProperties();
			initInputField("");
			initInputBox();
			initInputFlowPane();
			initRoot();
			initEventListeners();
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
			inputFlowPane = new FlowPane(6d, 10d);
			inputFlowPane.setRowValignment(VPos.CENTER);
			inputFlowPane.getChildren().add(inputBox);
		}
		
		private void initRoot() {
			setContent(inputFlowPane);
			setFitToWidth(true);
			setFillWidth(true);
		}
		
		private void initEventListeners() {
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
				else if(itemToRemove instanceof SmileyMessageItem) {
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
					addSmileyToCurrentSelectionRightSide(new SmileyMessageItem(getCurrentSelectionSmileyLeftSide().getFilePath()));
					removeSmileyFromCurrentSelectionLeftSide();
					return;
				}
				clearInputEmojis();
				
				if(previousItem instanceof ParagraphMessageItem) {
					newInputFieldText = "";
				}
				else if(previousItem instanceof SmileyMessageItem) {
					SmileyMessageItem smiley = (SmileyMessageItem)previousItem;
					newInputFieldText = "";
					addSmileyToCurrentSelectionLeftSide(new SmileyMessageItem(smiley.getFilePath()));
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
					addSmileyToCurrentSelectionLeftSide(new SmileyMessageItem(getCurrentSelectionSmileyRightSide().getFilePath()));
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
				else if(nextItem instanceof SmileyMessageItem) {
					SmileyMessageItem smiley = (SmileyMessageItem) nextItem;
					newInputFieldText = "";
					addSmileyToCurrentSelectionRightSide(new SmileyMessageItem(smiley.getFilePath()));
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
			else if(item instanceof SmileyMessageItem) {
				SmileyMessageItem smiley = (SmileyMessageItem)item;
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
		
		private SmileyMessageItem getCurrentSelectionSmileyLeftSide() {
			if(!hasCurrentSelectionSmileyLeftSide())
				return null;
			return (SmileyMessageItem) inputBox.getChildren().get(0);
		}
		
		private SmileyMessageItem getCurrentSelectionSmileyRightSide() {
			if(!hasCurrentSelectionSmileyRightSide())
				return null;
			return (SmileyMessageItem) inputBox.getChildren().get(inputBox.getChildren().size() > 2 ? 2 : 1);
		}
		
		private void addSmileyToCurrentSelectionLeftSide(SmileyMessageItem message) {
			if(message == null)
				return;
			removeSmileyFromCurrentSelectionLeftSide();
			inputBox.getChildren().add(0, message);
		}
		
		private void addSmileyToCurrentSelectionRightSide(SmileyMessageItem message) {
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
			else if(selectedItem instanceof SmileyMessageItem) {

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
				addItem(new SmileyMessageItem(getCurrentSelectionSmileyLeftSide().getFilePath()));
				++addedItems;
			}
			if(hasCurrentSelectionText())
				addedItems += addText(getCurrentText());
			if(hasCurrentSelectionSmileyRightSide()) {
				addItem(new SmileyMessageItem(getCurrentSelectionSmileyRightSide().getFilePath()));
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
			else if(newItem instanceof SmileyMessageItem) {
				SmileyMessageItem smiley = (SmileyMessageItem)newItem;
				smiley.getStyleClass().add("smiley-message-item");
				inputFlowPane.getChildren().add(index, new SmileyMessageItem(smiley.getFilePath()));	
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
			inputFlowPane.getChildren().clear();
			inputFlowPane.getChildren().add(inputField);
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
		
		public TextField getTextField() {
			return inputField;
		}
	}

	/*
	 *
	 */

	public class SmileySkinChooser extends HBox {
		private JFXFillTransition fillAnimation;
		private ObjectProperty<Duration> durationProperty;
		private ObjectProperty<Color> fromColorProperty;
		private ObjectProperty<Color> toColorProperty;
		private IntegerProperty currentColorIndexProperty;

		public final Color[] skinColors = { Color.web("ffd766"), Color.web("fae0c1"), Color.web("e3c29c"),
				Color.web("c6956c"), Color.web("a06940"), Color.web("5c473c") };

		public SmileySkinChooser() {
			getStyleClass().add("smiley-skin-chooser");
			
			Circle circle = new Circle(12.5d);
			circle.setFill(skinColors[0]);
			setShape(circle);
			setVisible(true);

			durationProperty = new SimpleObjectProperty<>(Duration.seconds(0.5));

			fromColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);
			fromColorProperty.addListener((obs, oldV, newV) -> {
				fillAnimation = new JFXFillTransition(getDuration(), this, oldV, newV);
				fillAnimation.playFromStart();
			});

			toColorProperty = new SimpleObjectProperty<>(skinColors[0]);
			toColorProperty.addListener((obs, oldV, newV) -> {
				fillAnimation = new JFXFillTransition(getDuration(), this, oldV, newV);
				fillAnimation.playFromStart();
			});

			currentColorIndexProperty = new SimpleIntegerProperty(0);
			currentColorIndexProperty.addListener((obs, oldV, newV) -> {
				int fromColorIndex = newV.intValue() - 1;
				int toColorIndex = newV.intValue();

				if (fromColorIndex < 0)
					fromColorIndex = skinColors.length - 1;
				else if (fromColorIndex >= skinColors.length)
					fromColorIndex = 0;

				if (toColorIndex < 0)
					toColorIndex = skinColors.length - 1;
				else if (toColorIndex >= skinColors.length)
					toColorIndex = 0;

				fromColorProperty.set(skinColors[fromColorIndex]);
				toColorProperty.set(skinColors[toColorIndex]);
			});

			fillAnimation = new JFXFillTransition();
			fillAnimation.setRegion(this);
			fillAnimation.durationProperty().bindBidirectional(durationProperty);
			fillAnimation.fromValueProperty().bindBidirectional(fromColorProperty);
			fillAnimation.toValueProperty().bindBidirectional(toColorProperty);
			fillAnimation.playFromStart();

			setOnMouseClicked(a -> handleAction(a));
		}

		public void handleAction(MouseEvent mouse) {
			if (mouse.getButton() == MouseButton.PRIMARY) {
				fillAnimation.playFromStart();
				currentColorIndexProperty.set((getCurrentColorIndex() + 1) >= skinColors.length ? 0 : currentColorIndexProperty.get() + 1);

				for (int i = 0; i < SmileyCategory.values().length; i++)
					smileyPane.initSmileys(SmileyCategory.getByInt(i), SmileySkinColor.getByInt(getCurrentColorIndex()), true);
			}
		}

		public ObjectProperty<Duration> durationProperty() {
			return durationProperty;
		}

		public Duration getDuration() {
			return durationProperty.get();
		}

		public void setDuration(Duration value) {
			durationProperty.set(value);
		}

		public ObjectProperty<Color> fromColorProperty() {
			return fromColorProperty;
		}

		public Color getFromColor() {
			return fromColorProperty.get();
		}

		public void setFromColor(Color value) {
			fromColorProperty.set(value);
		}

		public ObjectProperty<Color> toColorProperty() {
			return toColorProperty;
		}

		public Color getToColor() {
			return toColorProperty.get();
		}

		public void setToColor(Color value) {
			toColorProperty.set(value);
		}

		private int getCurrentColorIndex() {
			return currentColorIndexProperty.get();
		}
	}

	public class SmileyTabPane extends StackPane {
		private JFXTabPane tabPane;
		private SmileySkinChooser skinChooser;

		private SmileyTab smileyTabA;
		private SmileyTab smileyTabB;
		private SmileyTab smileyTabC;
		private SmileyTab smileyTabD;
		private SmileyTab smileyTabE;
		private SmileyTab smileyTabF;
		private SmileyTab smileyTabG;
		private SmileyTab smileyTabH;
		private SmileyTab smileyTabI;

		private final int CATEGORY_A_LENGTH = 289;
		private final int CATEGORY_B_LENGTH = 159;
		private final int CATEGORY_C_LENGTH = 86;
		private final int CATEGORY_D_LENGTH = 80;
		private final int CATEGORY_E_LENGTH = 119;
		private final int CATEGORY_F_LENGTH = 172;
		private final int CATEGORY_G_LENGTH = 257;
		private final int CATEGORY_H_LENGTH = 250;
		private final int CATEGORY_I_LENGTH = 0;

		public SmileyTabPane() {
			tabPane = new JFXTabPane();
			tabPane.setPickOnBounds(true);
			tabPane.setTabMaxHeight(35d);
			tabPane.setTabMinHeight(35d);

			smileyTabA = new SmileyTab();
			Label labelTabA = new Label("A");
			labelTabA.setFont(CUtils.CFont(15d));
			labelTabA.setTextFill(Color.DARKGRAY);
			smileyTabA.setGraphic(labelTabA);
			initSmileys(SmileyCategory.A, false);

			smileyTabB = new SmileyTab();
			Label labelTabB = new Label("B");
			labelTabB.setFont(CUtils.CFont(15d));
			labelTabB.setTextFill(Color.DARKGRAY);
			smileyTabB.setGraphic(labelTabB);
			initSmileys(SmileyCategory.B, false);

			smileyTabC = new SmileyTab();
			Label labelTabC = new Label("C");
			labelTabC.setFont(CUtils.CFont(15d));
			labelTabC.setTextFill(Color.DARKGRAY);
			smileyTabC.setGraphic(labelTabC);
			initSmileys(SmileyCategory.C, false);

			smileyTabD = new SmileyTab();
			Label labelTabD = new Label("D");
			labelTabD.setFont(CUtils.CFont(15d));
			labelTabD.setTextFill(Color.DARKGRAY);
			smileyTabD.setGraphic(labelTabD);
			initSmileys(SmileyCategory.D, false);

			smileyTabE = new SmileyTab();
			Label labelTabE = new Label("E");
			labelTabE.setFont(CUtils.CFont(15d));
			labelTabE.setTextFill(Color.DARKGRAY);
			smileyTabE.setGraphic(labelTabE);
			initSmileys(SmileyCategory.E, false);

			smileyTabF = new SmileyTab();
			Label labelTabF = new Label("F");
			labelTabF.setFont(CUtils.CFont(15d));
			labelTabF.setTextFill(Color.DARKGRAY);
			smileyTabF.setGraphic(labelTabF);
			initSmileys(SmileyCategory.F, false);

			smileyTabG = new SmileyTab();
			Label labelTabG = new Label("G");
			labelTabG.setFont(CUtils.CFont(15d));
			labelTabG.setTextFill(Color.DARKGRAY);
			smileyTabG.setGraphic(labelTabG);
			initSmileys(SmileyCategory.G, false);

			smileyTabH = new SmileyTab();
			Label labelTabH = new Label("H");
			labelTabH.setFont(CUtils.CFont(15d));
			labelTabH.setTextFill(Color.DARKGRAY);
			smileyTabH.setGraphic(labelTabH);
			initSmileys(SmileyCategory.H, false);

			smileyTabI = new SmileyTab();
			Label labelTabI = new Label("I");
			labelTabI.setFont(CUtils.CFont(15d));
			labelTabI.setTextFill(Color.DARKGRAY);
			smileyTabI.setGraphic(labelTabI);
			initSmileys(SmileyCategory.I, false);

			skinChooser = new SmileySkinChooser();
			skinChooser.setFromColor(skinChooser.skinColors[0]);
			skinChooser.setPickOnBounds(true);
			GUITools.setFixedSizeOf(skinChooser, 25d, 25d);
			SmileyTabPane.setAlignment(skinChooser, Pos.TOP_RIGHT);
			SmileyTabPane.setMargin(skinChooser, new Insets(0d, 15d, 5d, 5d));

			tabPane.getTabs().addAll(smileyTabA, smileyTabB, smileyTabC, smileyTabD, smileyTabE, smileyTabF,
					smileyTabG, smileyTabH, smileyTabI);

			getChildren().addAll(tabPane, skinChooser);
		}

		private void initSmileys(SmileyCategory category, SmileySkinColor color, boolean override) {
			final SmileyTab smileyTab;
			final int smileyCount;
			final String smileyCategory = category.name();

			switch (category) {
			case A:
				smileyTab = smileyTabA;
				smileyCount = CATEGORY_A_LENGTH;
				break;
			case B:
				smileyTab = smileyTabB;
				smileyCount = CATEGORY_B_LENGTH;
				break;
			case C:
				smileyTab = smileyTabC;
				smileyCount = CATEGORY_C_LENGTH;
				break;
			case D:
				smileyTab = smileyTabD;
				smileyCount = CATEGORY_D_LENGTH;
				break;
			case E:
				smileyTab = smileyTabE;
				smileyCount = CATEGORY_E_LENGTH;
				break;
			case F:
				smileyTab = smileyTabF;
				smileyCount = CATEGORY_F_LENGTH;
				break;
			case G:
				smileyTab = smileyTabG;
				smileyCount = CATEGORY_G_LENGTH;
				break;
			case H:
				smileyTab = smileyTabH;
				smileyCount = CATEGORY_H_LENGTH;
				break;
			case I:
				smileyTab = smileyTabI;
				smileyCount = CATEGORY_I_LENGTH;
				break;
			default:
				smileyCount = 1;
				smileyTab = null;
			}

			new Thread(() -> {
				for (int i = 0; i < smileyCount; i++) {
					boolean[] withSkinColors = { false };
					int[] tempIndex = { i };
					String currentImageTitle = smileyCategory + (i + 1);
					Image currentImage;
					ImageView currentImageView;

					try {
						currentImage = new Image("/resources/smileys/category" + smileyCategory + "/" + currentImageTitle + ".png");
						withSkinColors[0] = false;
					} 
					catch (IllegalArgumentException e)
					{
						currentImage = new Image("/resources/smileys/category" + smileyCategory + "/" + currentImageTitle + smileySkinColorToString(color) + ".png");
						withSkinColors[0] = true;
					}

					currentImageView = new ImageView(currentImage);
					currentImageView.setSmooth(true);
					currentImageView.setCache(true);
					currentImageView.getStyleClass().add("smiley");
					currentImageView.setOnMouseClicked(a -> onSmileyClicked(/*":" + */currentImageTitle
							+ (withSkinColors[0] ? smileySkinColorToString(color) : "")/* + ":"*/));

					Platform.runLater(() -> {
						if (override)
							smileyTab.setSmiley(tempIndex[0], currentImageView);
						else
							smileyTab.addSmiley(currentImageView);
					});
				}
			}).start();
			;
		}

		private void initSmileys(SmileyCategory category, boolean override) {
			initSmileys(category, SmileySkinColor.YELLOW, override);
		}

		private String smileySkinColorToString(SmileySkinColor color) {
			switch (color) {
			case YELLOW:
				return "A";
			case LIGHT_WHITE:
				return "B";
			case WHITE:
				return "C";
			case DEEP_WHITE:
				return "D";
			case LIGHT_BLACK:
				return "E";
			case BLACK:
				return "F";
			default:
				return null;
			}
		}

		private void onSmileyClicked(String smileyText) {
			String currentText = inputField.getCurrentText();
			int currentTextPos = inputField.getOldCaretPosition();
			
			System.out.println("Current Text: " + currentText);
			System.out.println("Current Pos: " + currentTextPos);
			
			if(!currentText.isEmpty()) {
				String firstWord = currentText.substring(0, currentTextPos);
				String secondWord = currentText.substring(currentTextPos);
				
				System.out.println("First Word: " + firstWord);
				System.out.println("Second Word: " + secondWord);
				
				if(!firstWord.isEmpty())
					inputField.addText(firstWord);
				inputField.addItem(new SmileyMessageItem("/resources/smileys/category" + smileyText.charAt(0) + "/" + smileyText + ".png", smileyText));
				if(!secondWord.isEmpty())
					inputField.addText(secondWord);
			}			
			else
				inputField.addItem(new SmileyMessageItem("/resources/smileys/category" + smileyText.charAt(0) + "/" + smileyText + ".png", smileyText));
			inputField.getTextField().requestFocus();
		}
	}

	public class SmileyTab extends Tab {
		private ScrollPane smileyScrollPane;
		private FlowPane smileyFlowPane;

		public SmileyTab() {
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
}