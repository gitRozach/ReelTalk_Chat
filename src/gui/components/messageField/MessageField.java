package gui.components.messageField;

import com.jfoenix.controls.JFXNodesList;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
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
	private MaterialDesignIconView emojiButton;
	private JFXNodesList fileButtons;
	private FontAwesomeIconView fileButton;
	private FontAwesomeIconView fileButton1;
	private FontAwesomeIconView fileButton2;
	
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
		FXUtils.setFixedHeightOf(emojiTextField, 60d);

		emojiButton = new MaterialDesignIconView(MaterialDesignIcon.EMOTICON);//new ImageView(new Image("/resources/icons/img_smiley.png"));
		emojiButton.setGlyphSize(40d);
		emojiButton.setSmooth(true);
		emojiButton.getStyleClass().add("smiley-button");
		emojiButton.setPickOnBounds(true);
		emojiButton.setOnMouseClicked(a -> onEmojiButtonClicked.handle(a));

		fileButtons = new JFXNodesList();
		fileButtons.setRotate(90d);
		fileButtons.setSpacing(10d);
		
		fileButton = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
		fileButton.setGlyphSize(40d);
		fileButton.setSmooth(true);
		fileButton.getStyleClass().add("file-button");
		fileButton.setPickOnBounds(true);
		fileButton.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		fileButton1 = new FontAwesomeIconView(FontAwesomeIcon.FILE_IMAGE_ALT);
		fileButton1.setGlyphSize(25d);
		fileButton1.setSmooth(true);
		fileButton1.getStyleClass().add("file-button");
		fileButton1.setPickOnBounds(true);
		fileButton1.setOnMouseClicked(a -> onFileButtonClicked.handle(a));
		
		fileButton2 = new FontAwesomeIconView(FontAwesomeIcon.FILE_AUDIO_ALT);
		fileButton2.setGlyphSize(25d);
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
		messageBox = new HBox(10d);
		messageBox.setFillHeight(true);
		messageBox.getStyleClass().add("message-box");
		messageBox.setOnDragOver(getOnDragOver());
		messageBox.setOnDragDropped(getOnDragDropped());
		messageBox.getChildren().addAll(emojiButton, emojiTextField, fileButtons);
		HBox.setHgrow(emojiTextField, Priority.ALWAYS);
		HBox.setMargin(emojiButton, new Insets(15d, 0d, 0d, 10d));
		HBox.setMargin(emojiTextField, new Insets(15d, 0d, 0d, 0d));
		HBox.setMargin(fileButtons, new Insets(15d, 15d, 0d, 0d));
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
		messageBox.setOnMouseClicked(a -> emojiTextField.getInputField().requestFocus());
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
		if (!isOpen() && hideEmojiTabPaneAnimation.getStatus() != Status.RUNNING) {
			setOpen(true);
			showEmojiTabPaneAnimation.playFromStart();
		}
	}

	public void closeEmojiPane() {
		if (isOpen() && showEmojiTabPaneAnimation.getStatus() != Status.RUNNING) {
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

	public MaterialDesignIconView getEmojiButton() {
		return emojiButton;
	}

	public FontAwesomeIconView getFileButton() {
		return fileButton;
	}
}