package gui.client.components.messageField;

import com.jfoenix.controls.JFXNodesList;

import gui.animations.Animations;
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
import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import utils.JFXUtils;

public class MessageField extends VBox {
	private HBox messageBox;

	private EmojiTabPane emojiPane;
	private EmojiTextField emojiTextField;
	private ImageView emojiButton;
	private ImageView fileButton;

	private BooleanProperty openProperty;

	private EventHandler<KeyEvent> onEnterPressed;
	private EventHandler<MouseEvent> onEmojiButtonClicked;
	private EventHandler<MouseEvent> onFileButtonClicked;
	private ObjectEventHandler<String> onEmojiPressed;

	private Animation smileyIn;
	private Animation smileyOut;

	public MessageField() {
		getStylesheets().add("/stylesheets/client/MessageField.css");

		messageBox = new HBox();
		messageBox.setFillHeight(true);
		messageBox.getStyleClass().add("message-box");
		messageBox.setOnDragOver(getOnDragOver());
		messageBox.setOnDragDropped(getOnDragDropped());
		messageBox.setPadding(new Insets(5d, 10d, 5d, 5d));

		openProperty = new SimpleBooleanProperty(false);

		emojiPane = new EmojiTabPane();
		emojiPane.setMinHeight(0d);
		emojiPane.setPrefHeight(0d);
		emojiPane.setMaxHeight(0d);
		emojiPane.setOpacity(0d);

		onEnterPressed = (keyEvent -> {});
		onEmojiButtonClicked = (mouseEvent -> {
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {
				if (isOpen())
					closeEmojiPane();
				else
					openEmojiPane();
			}
		});
		onFileButtonClicked = (mouseEvent -> {});
		
		emojiTextField = new EmojiTextField();
		JFXUtils.setFixedHeightOf(emojiTextField, 50d);
		HBox.setHgrow(emojiTextField, Priority.ALWAYS);

		emojiButton = new ImageView(new Image("/resources/icons/img_smiley.png"));
		emojiButton.setSmooth(true);
		emojiButton.getStyleClass().add("smiley-button");
		emojiButton.setPickOnBounds(true);
		emojiButton.setOnMouseClicked(a -> onEmojiButtonClicked.handle(a));

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

		Animation resizeInAnimation = Animations.newResizeAnimation(emojiPane, Duration.seconds(0.4d), false, 0d, 200d,
				Interpolator.EASE_BOTH);
		Animation fadeInAnimation = Animations.newFadeAnimation(emojiPane, Duration.seconds(0.5d), 0d, 1d,
				Interpolator.EASE_BOTH);

		Animation resizeOutAnimation = Animations.newResizeAnimation(emojiPane, Duration.seconds(0.3d), false, 200d,
				0d, Interpolator.EASE_BOTH);
		Animation fadeOutAnimation = Animations.newFadeAnimation(emojiPane, Duration.seconds(0.4d), 1d, 0d,
				Interpolator.EASE_BOTH);

		smileyIn = Animations.newParallelTransition(Interpolator.EASE_BOTH, 1, false, resizeInAnimation,
				fadeInAnimation);
		smileyOut = Animations.newParallelTransition(Interpolator.EASE_BOTH, 1, false, resizeOutAnimation,
				fadeOutAnimation);

		messageBox.getChildren().addAll(emojiButton, emojiTextField, fileButtons);

		getChildren().addAll(messageBox, emojiPane);
	}

	public void openEmojiPane() {
		if (!isOpen() && smileyIn != null && smileyOut.getStatus() != Status.RUNNING) {
			setOpen(true);
			smileyIn.playFromStart();
		}
	}

	public void closeEmojiPane() {
		if (isOpen() && smileyOut != null && smileyIn.getStatus() != Status.RUNNING) {
			setOpen(false);
			smileyOut.playFromStart();
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