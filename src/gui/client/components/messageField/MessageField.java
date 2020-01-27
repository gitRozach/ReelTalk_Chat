package gui.client.components.messageField;

import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXTabPane;
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
import javafx.event.ActionEvent;
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
	private HBox messageBox;

	private EmojiTabPane smileyPane;
	private EmojiTextField inputField;
	private ImageView smileyButton;
	private ImageView fileButton;

	private BooleanProperty openProperty;

	// EventHandler
	private EventHandler<? super KeyEvent> onEnterPressed;
	private EventHandler<? super MouseEvent> onSmileyButtonClicked;
	private EventHandler<? super MouseEvent> onFileButtonClicked;
	private EventHandler<ActionEvent> onEmojiPressed;
	// Animations
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

		smileyPane = new EmojiTabPane();
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
		
		onEmojiPressed = event -> {
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
		};
		
		inputField = new EmojiTextField();
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

	public EmojiTabPane getSmileyPane() {
		return smileyPane;
	}

	public EmojiTextField getInputField() {
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
}