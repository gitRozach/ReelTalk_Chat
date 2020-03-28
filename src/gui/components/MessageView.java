package gui.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gui.components.contextMenu.CustomContextMenu;
import gui.components.contextMenu.MenuItemButton;
import gui.components.messages.GUIMessage;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MessageView extends StackPane {
	private ScrollPane rootContent;
	private StackPane messagesAndLoadButton;
	private VBox messages;
	private HBox loadButton;
	private CustomContextMenu contextMenu;
	
	private DoubleProperty scrollValueVerticalProperty;
	
	private int maxMessages;
	private int maxMessagesAtInit;
	private int loadValue;
	private List<Double> messageYPositions;
	private ObservableList<GUIMessage> unloadedMessages;

	public MessageView() {
		getStyleClass().add("message-view");
		getStylesheets().add("/stylesheets/client/defaultStyle/MessageView.css");
		
		scrollValueVerticalProperty = new SimpleDoubleProperty(0d);
		
		maxMessages = 20;
		maxMessagesAtInit = maxMessages;
		loadValue = 20;
		
		messageYPositions = new ArrayList<Double>();
		unloadedMessages = FXCollections.observableArrayList();

		rootContent = new ScrollPane();
		scrollValueVerticalProperty.bindBidirectional(rootContent.vvalueProperty());
		
		rootContent.setOnMouseEntered(a -> {
			Node bar1 = rootContent.lookup(".scroll-bar:horizontal");
			Node bar2 = rootContent.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(1d);
			if(bar2 != null)
				bar2.setOpacity(1d);
		});
		
		rootContent.setOnMouseExited(b -> {
			Node bar1 = rootContent.lookup(".scroll-bar:horizontal");
			Node bar2 = rootContent.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(0d);
			if(bar2 != null)
				bar2.setOpacity(0d);
		});
		
		rootContent.setFitToWidth(true);
		messagesAndLoadButton = new StackPane();

		messages = new VBox(0d);
		messages.setFillWidth(true);
		messages.setAlignment(Pos.TOP_LEFT);
		messages.setPadding(new Insets(0d));
		//this.messages.setSpacing(0d);

		loadButton = new HBox();
		loadButton.visibleProperty().bind(Bindings.isEmpty(unloadedMessages).not());
		loadButton.setStyle("-fx-background-color: rgb(0, 0, 0, 0.7);");
		loadButton.setMinHeight(40d);
		loadButton.setMaxHeight(40d);
		loadButton.setAlignment(Pos.CENTER);
		loadButton.setPadding(new Insets(10d));
		loadButton.setSpacing(10d);

		contextMenu = createContextMenu();

		Label loadLabel = new Label("Weitere Nachrichten anzeigen");
		loadLabel.setOnMouseClicked(a -> this.onLoadClicked());
		loadLabel.setStyle("-fx-text-fill: white;");
		loadLabel.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 15d));

		loadButton.getChildren().add(loadLabel);

		StackPane.setAlignment(loadButton, Pos.TOP_CENTER);
		messagesAndLoadButton.getChildren().addAll(messages, loadButton);

		setOnContextMenuRequested(a -> onContextMenuRequested(a));
		setOnMouseClicked(b -> onMouseClicked(b));

		rootContent.setContent(messagesAndLoadButton);
		//loadContent(rootContent);
		getChildren().add(rootContent);
	}

	public void addMessageAnimated(GUIMessage message) {
		message.setMinWidth(100d);
		message.setMinHeight(0d);

		addMessage(message);
		ParallelTransition animation = createAddAnimation(message, Duration.seconds(0.25), Interpolator.EASE_OUT);
		message.setMaxHeight(Double.MAX_VALUE);
		message.setOpacity(1d);
		animation.playFromStart();
	}

	public void addMessage(GUIMessage message) {
		addMessage(messageCount(), message);
	}

	public void addMessage(int index, GUIMessage message) {
		Platform.runLater(() -> {
			HBox root = new HBox(message);
			root.setAlignment(Pos.TOP_LEFT);
			HBox.setHgrow(message, Priority.SOMETIMES);
			root.applyCss();
			root.layout();
			messages.getChildren().add(index, root);
			//ADD y pos
			
			if (this.messageCount() > maxMessages) {
				GUIMessage firstMessage = getFirstMessage();
				if (firstMessage != null) {
					unloadedMessages.add(firstMessage);
					removeMessage(firstMessage);
				}
			}
		});
	}

	public void addMessages(GUIMessage... messages) {
		int currentIndex = getMessages().getChildren().size();
		for(GUIMessage message : messages)
			addMessage(currentIndex, message);
	}

	public void removeMessage(GUIMessage message) {
		this.removeMessage(indexOf(message));
	}

	public void removeMessage(int index) {
		Platform.runLater(() -> {
			if (index >= 0 && index < messageCount())
				messages.getChildren().remove(index);
		});
	}

	public void clear() {
		Platform.runLater(() -> {
			maxMessages = maxMessagesAtInit;
			unloadedMessages.clear();
			messages.getChildren().clear();
		});
	}

	public GUIMessage getFirstMessage() {
		if (messageCount() > 0) {
			HBox firstNode = (HBox) messages.getChildren().get(0);
			for (Node n : firstNode.getChildren()) {
				if (n instanceof GUIMessage)
					return (GUIMessage) n;
			}
			return null;
		} else
			return null;
	}

	public GUIMessage getLastMessage() {
		int messageCount = messageCount();
		if (messageCount > 0) {
			HBox lastNode = (HBox) messages.getChildren().get(messageCount - 1);
			for (Node n : lastNode.getChildren()) {
				if (n instanceof GUIMessage)
					return (GUIMessage) n;
			}
			return null;
		} else
			return null;
	}

	public int indexOf(GUIMessage message) {
		int index = 0;
		for (Node n : messages.getChildren()) {
			if (n instanceof GUIMessage && ((GUIMessage) n).equals(message))
				return index;
			index++;
		}
		return -1;
	}

	public boolean contains(GUIMessage message) {
		return indexOf(message) != -1;
	}

	/*
	 *
	 */

	private CustomContextMenu createContextMenu() {
		MenuItemButton m1 = new MenuItemButton("Hintergrund aendern");
		m1.setOnMouseClicked(a -> {
			Platform.runLater(() -> {
				if (a.getButton() == MouseButton.PRIMARY) {
					FileChooser fc = new FileChooser();
					fc.getExtensionFilters().addAll(
							new FileChooser.ExtensionFilter("Bilder", "*.png", "*.jpeg", "*.jpg", "*.bmp", "*.gif"));
					File imgFile = fc.showOpenDialog(null);
					if (imgFile != null) {
						try {
							//setBackground(new Background(new BackgroundImage(new Image(imgFile.toURI().toURL().toString()), BackgroundRepeat.SPACE, BackgroundRepeat.SPACE, BackgroundPosition.CENTER, new BackgroundS)));
							//setBackground(new Background(new BackgroundFill(Color.rgb(64, 64, 64, 0d), new CornerRadii(100d), new Insets(5d))));
							
							setStyle("-fx-background-image: url(\"" + imgFile.toURI().toURL() + "\");" + "-fx-background: rgba(64, 64, 64, 0);");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		});

		CustomContextMenu menu = new CustomContextMenu(m1);
		menu.setAnimationTranslateWidth(100d);
		return menu;
	}

	private ParallelTransition createAddAnimation(GUIMessage message, Duration duration, Interpolator interpolator) {
		message.applyCss();
		message.layout();

		ScaleTransition growAnimation = new ScaleTransition(duration, message);
		growAnimation.setFromY(0.5d);
		growAnimation.setToY(1d);

		Timeline move1 = new Timeline();
		Timeline move2 = new Timeline();
		KeyValue moveValue1 = null;
		KeyValue moveValue2 = null;

		moveValue1 = new KeyValue(message.translateXProperty(), 0d);
		moveValue2 = new KeyValue(message.translateXProperty(), 0d);
		message.setTranslateX(0d);

		KeyFrame moveFrame1 = new KeyFrame(duration.multiply(0.66d), moveValue1);
		KeyFrame moveFrame2 = new KeyFrame(duration.multiply(0.34d), moveValue2);
		move1.getKeyFrames().add(moveFrame1);
		move2.getKeyFrames().add(moveFrame2);

		SequentialTransition moveAnimation = new SequentialTransition(move1, move2);
		ParallelTransition addAnimation = new ParallelTransition(growAnimation, moveAnimation);
		addAnimation.setInterpolator(interpolator);
		return addAnimation;
	}

	private void onLoadClicked() {
		Platform.runLater(() -> {
			if (!unloadedMessages.isEmpty()) {
				int unloadedSize = unloadedMessages.size();
				int loading = unloadedSize < loadValue ? unloadedSize : loadValue;
				maxMessages += loadValue;
				for (int i = 0; i < loading; i++) {
					GUIMessage currentMessage = unloadedMessages.get(unloadedSize - 1);
					addMessage(0, currentMessage);
					unloadedMessages.remove(unloadedSize - 1);
					unloadedSize = unloadedMessages.size();
				}
			}
		});
	}

	private void onContextMenuRequested(ContextMenuEvent menu) {
		Platform.runLater(() -> {
			contextMenu.showAnimated(this, menu.getScreenX(), menu.getScreenY());
			menu.consume();
		});
	}

	private void onMouseClicked(MouseEvent mouse) {
		Platform.runLater(() -> {
			if (mouse.getButton() == MouseButton.PRIMARY && contextMenu.isAutoHide())
				contextMenu.hide();
		});
	}

	/*
	 *
	 */

	public VBox getMessages() {
		return messages;
	}

	public HBox getLoadButton() {
		return loadButton;
	}

	public ReadOnlyDoubleProperty scrollValueVerticalProperty() {
		return scrollValueVerticalProperty;
	}
	
	public double getScrollValueVertical() {
		return scrollValueVerticalProperty.get();
	}
	
	public void setScrollValueVertical(double value) {
		scrollValueVerticalProperty.set(value);
	}
	
	public int getMaxMessages() {
		return maxMessages;
	}

	public int getMaxMessagesAtInit() {
		return maxMessagesAtInit;
	}

	public int getLoadValue() {
		return loadValue;
	}

	public int messageCount() {
		return messages.getChildren().size();
	}
	
	public List<Double> getMessageYPositions() {
		return messageYPositions;
	}

	public ObservableList<GUIMessage> getUnloadedMessages() {
		return unloadedMessages;
	}
}
