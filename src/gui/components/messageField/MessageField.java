package gui.components.messageField;

import com.jfoenix.controls.JFXNodesList;

import gui.animations.Animations;
import gui.components.messageField.items.EmojiMessageItem;
import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import utils.FXUtils;

public class MessageField extends VBox {
	private BooleanProperty openProperty;
	
	private HBox messageBox;
	private EmojiTextField emojiTextField;
	private ImageView emojiButton;
	private JFXNodesList fileButtons;
	private ImageView fileButton;
	private ImageView fileButton1;
	private ImageView fileButton2;
	
	private EmojiTabPane emojiPane;

	private EventHandler<KeyEvent> onEnterPressed;
	private EventHandler<MouseEvent> onEmojiButtonClicked;
	private EventHandler<MouseEvent> onFileButtonClicked;
	private ObjectEventHandler<String> onEmojiPressed;

	private Animation showEmojiTabPaneAnimation;
	private Animation hideEmojiTabPaneAnimation;

	public MessageField() {
		initialize();
	}
	
	public void initialize() {
		initStylesheets();
		initProperties();
		initControls();
		initEmojiTabPane();
		initMessageBox();
		initAnimations();
		initEventHandlers();
		initRoot();
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/defaultStyle/MessageField.css");
	}
	
	private void initProperties() {
		openProperty = new SimpleBooleanProperty(false);
	}
	
	private void initControls() {
		emojiTextField = new EmojiTextField();
		FXUtils.setFixedHeightOf(emojiTextField, 50d);

		emojiButton = new ImageView(new Image("/resources/icons/img_smiley.png"));
		emojiButton.setSmooth(true);
		emojiButton.getStyleClass().add("smiley-button");
		emojiButton.setPickOnBounds(true);
		emojiButton.setOnMouseClicked(a -> onEmojiButtonClicked.handle(a));

		fileButtons = new JFXNodesList();
		fileButtons.setRotate(90d);
		fileButtons.setSpacing(10d);
		
		fileButton = new ImageView(new Image("/resources/icons/img_file.png"));
		fileButton.setSmooth(true);
		fileButton.getStyleClass().add("file-button");
		fileButton.setPickOnBounds(true);
		fileButton.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		fileButton1 = new ImageView(new Image("/resources/icons/img_file.png"));
		fileButton1.setSmooth(true);
		fileButton1.getStyleClass().add("file-button");
		fileButton1.setPickOnBounds(true);
		fileButton1.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		fileButton2 = new ImageView(new Image("/resources/icons/img_file.png"));
		fileButton2.setSmooth(true);
		fileButton2.getStyleClass().add("file-button");
		fileButton2.setPickOnBounds(true);
		fileButton2.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		fileButtons.addAnimatedNode(new VBox(fileButton));
		fileButtons.addAnimatedNode(new VBox(fileButton1));
		fileButtons.addAnimatedNode(new VBox(fileButton2));
	}
	
	private void initEmojiTabPane() {
		emojiPane = new EmojiTabPane();
		emojiPane.getEmojiSkinChooser().setOnSkinChooserClicked(a -> onSkinChooserClicked());
		emojiPane.setMinHeight(0d);
		emojiPane.setPrefHeight(0d);
		emojiPane.setMaxHeight(0d);
		emojiPane.setOpacity(0d);
	}
	
	private void initMessageBox() {
		messageBox = new HBox();
		messageBox.setFillHeight(true);
		messageBox.getStyleClass().add("message-box");
		messageBox.setOnDragOver(getOnDragOver());
		messageBox.setOnDragDropped(getOnDragDropped());
		messageBox.setPadding(new Insets(5d, 10d, 5d, 5d));
		messageBox.getChildren().addAll(emojiButton, emojiTextField, fileButtons);
		HBox.setHgrow(emojiTextField, Priority.ALWAYS);
	}
	
	private void initAnimations() {
		Animation resizeStartAnimation = Animations.newResizeAnimation(emojiPane, Duration.seconds(0.4d), false, 0d, 200d, Interpolator.EASE_BOTH);
		Animation fadeStartAnimation = Animations.newFadeAnimation(emojiPane, Duration.seconds(0.5d), 0d, 1d, Interpolator.EASE_BOTH);
		showEmojiTabPaneAnimation = Animations.newParallelTransition(Interpolator.EASE_BOTH, 1, false, resizeStartAnimation, fadeStartAnimation);
		
		Animation resizeHideAnimation = Animations.newResizeAnimation(emojiPane, Duration.seconds(0.3d), false, 200d, 0d, Interpolator.EASE_BOTH);
		Animation fadeHideAnimation = Animations.newFadeAnimation(emojiPane, Duration.seconds(0.4d), 1d, 0d, Interpolator.EASE_BOTH);
		hideEmojiTabPaneAnimation = Animations.newParallelTransition(Interpolator.EASE_BOTH, 1, false, resizeHideAnimation, fadeHideAnimation);		
	}
	
	private void initEventHandlers() {
		onEnterPressed = (keyEvent -> {});
		onEmojiButtonClicked = mouseEvent -> onEmojiButtonClicked(mouseEvent);
		onFileButtonClicked = mouseEvent -> onFileButtonClicked(mouseEvent);
		setOnEmojiPressed(new ObjectEventHandler<String>() {
			@Override
			public void handle(ObjectEvent<String> event) {
				onEmojiClicked(event.getAttachedObject());
			}
		});
	}
	
	private void initRoot() {
		getChildren().addAll(messageBox, emojiPane);
	}
	
	public void onEmojiButtonClicked(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			if (isOpen())
				closeEmojiPane();
			else
				openEmojiPane();
		}
	}
	
	public void onFileButtonClicked(MouseEvent event) {
		
	}
	
	public void onEmojiClicked(String emojiString) {
		String currentText = emojiTextField.getCurrentText();
		int currentTextPos = emojiTextField.getOldCaretPosition();
		
		if(!currentText.isEmpty()) {
			String firstWord = currentText.substring(0, currentTextPos);
			String secondWord = currentText.substring(currentTextPos);
			
			if(!firstWord.isEmpty())
				emojiTextField.addText(firstWord);
			emojiTextField.addItem(new EmojiMessageItem("/resources/smileys/category" + emojiString.charAt(0) + "/" + emojiString + ".png", emojiString));
			if(!secondWord.isEmpty())
				emojiTextField.addText(secondWord);
		}			
		else
			emojiTextField.addItem(new EmojiMessageItem("/resources/smileys/category" + emojiString.charAt(0) + "/" + emojiString + ".png", emojiString));
		emojiTextField.getInputField().requestFocus();	
	}

	public void onSkinChooserClicked() {
		int nextIndex = emojiPane.getEmojiSkinChooser().nextColorIndex();
		emojiPane.initAllEmojisWithSkinColor(EmojiSkinColor.getByInt(nextIndex));
	}

	public void openEmojiPane() {
		if (!isOpen() && showEmojiTabPaneAnimation != null && hideEmojiTabPaneAnimation.getStatus() != Status.RUNNING) {
			setOpen(true);
			showEmojiTabPaneAnimation.playFromStart();
		}
	}

	public void closeEmojiPane() {
		if (isOpen() && hideEmojiTabPaneAnimation != null && showEmojiTabPaneAnimation.getStatus() != Status.RUNNING) {
			setOpen(false);
			hideEmojiTabPaneAnimation.playFromStart();
		}
	}
	
	public String getText() {
		return emojiTextField.getText();
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

	public EmojiTabPane getSmileyPane() {
		return emojiPane;
	}

	public EmojiTextField getTextField() {
		return emojiTextField;
	}

	public EventHandler<? super KeyEvent> getOnEnterPressed() {
		return onEnterPressed;
	}

	public void setOnEnterPressed(EventHandler<KeyEvent> ke) {
		onEnterPressed = ke;
		addEventHandler(KeyEvent.KEY_TYPED, onEnterPressed);
	}

	public EventHandler<? super MouseEvent> getOnEmojiButtonClicked() {
		return onEmojiButtonClicked;
	}

	public void setOnEmojiButtonClicked(EventHandler<MouseEvent> me) {
		onEmojiButtonClicked = me;
	}

	public EventHandler<? super MouseEvent> getOnFileButtonClicked() {
		return onFileButtonClicked;
	}

	public void setOnFileButtonClicked(EventHandler<MouseEvent> me) {
		onFileButtonClicked = me;
	}
	
	public ObjectEventHandler<String> getOnEmojiPressed() {
		return onEmojiPressed;
	}

	public void setOnEmojiPressed(ObjectEventHandler<String> handler) {
		onEmojiPressed = handler;
		addEventFilter(ObjectEvent.STRING, onEmojiPressed);
	}
	
	public HBox getMessageBox() {
		return messageBox;
	}

	public EmojiTabPane getEmojiPane() {
		return emojiPane;
	}

	public EmojiTextField getEmojiTextField() {
		return emojiTextField;
	}

	public ImageView getEmojiButton() {
		return emojiButton;
	}

	public ImageView getFileButton() {
		return fileButton;
	}
}