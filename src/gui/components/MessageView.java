package gui.components;

import java.io.File;

import gui.components.contextMenu.CustomContextMenu;
import gui.components.contextMenu.MenuItemButton;
import gui.components.messages.ChatViewMessage;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import utils.FXUtils;

public class MessageView extends StackPane {	
	private ScrollPane scrollPane;
	private StackPane messagesAndLoadButton;
	private VBox messages;
	private HBox loadButton;
	private CustomContextMenu contextMenu;
		
	private BooleanProperty loadingProperty;
	private IntegerProperty maxMessagesProperty;
	private IntegerProperty maxMessagesAtInitProperty;
	private IntegerProperty loadValueProperty;
	private ObservableList<ChatViewMessage> unloadedMessages;

	public MessageView() {
		initStylesheets();
		initProperties();
		initControls();
		initRoot();
		initEventHandlers();
	}
	
	public void addMessageAnimated(ChatViewMessage message) {
		ParallelTransition animation = createAddAnimation(message, Duration.seconds(0.25), Interpolator.EASE_OUT);
		
		message.setMinWidth(100d);
		message.setMinHeight(0d);
		addMessage(message);
		
		message.setMaxHeight(Double.MAX_VALUE);
		message.setOpacity(1d);
		animation.playFromStart();
	}

	public void addMessage(ChatViewMessage message) {
		Platform.runLater(() -> {
			messages.getChildren().add(message);	
			if (getLoadedMessageCount() > getMaxMessages()) {
				ChatViewMessage firstMessage = getFirstMessage();
				if (firstMessage != null && unloadedMessages.add(firstMessage))
					removeMessage(firstMessage);
			}
			setScrollValueVertical(1d);
		});
	}

	public void addMessage(int index, ChatViewMessage message) {
		Platform.runLater(() -> {
			messages.getChildren().add(index, message);
			if (getLoadedMessageCount() > getMaxMessages()) {
				ChatViewMessage firstMessage = getFirstMessage();
				if (firstMessage != null && unloadedMessages.add(firstMessage))
					removeMessage(firstMessage);
			}
		});
	}

	public void addMessages(ChatViewMessage... messages) {
		for(ChatViewMessage message : messages)
			addMessage(message);
	}

	public void removeMessage(ChatViewMessage message) {
		removeMessage(indexOf(message));
	}

	public void removeMessage(int index) {
		Platform.runLater(() -> {
			if (index >= 0 && index < getLoadedMessageCount())
				messages.getChildren().remove(index);	
		});
	}

	public void clear() {
		Platform.runLater(() -> {
			setMaxMessages(getMaxMessagesAtInit());
			unloadedMessages.clear();
			messages.getChildren().clear();
		});
	}

	public ChatViewMessage getFirstMessage() {
		if (getLoadedMessageCount() > 0) {
			ChatViewMessage firstNode = (ChatViewMessage) messages.getChildren().get(0);
			for (Node n : firstNode.getChildren()) {
				if (n instanceof ChatViewMessage)
					return (ChatViewMessage) n;
			}
			return null;
		} 
		else
			return null;
	}

	public ChatViewMessage getLastMessage() {
		int messageCount = getLoadedMessageCount();
		if (messageCount > 0) {
			ChatViewMessage lastNode = (ChatViewMessage) messages.getChildren().get(messageCount - 1);
			for (Node n : lastNode.getChildren()) {
				if (n instanceof ChatViewMessage)
					return (ChatViewMessage) n;
			}
			return null;
		} 
		else
			return null;
	}

	public int indexOf(ChatViewMessage message) {
		int index = 0;
		for (Node n : messages.getChildren()) {
			if (n instanceof ChatViewMessage && ((ChatViewMessage) n).equals(message))
				return index;
			index++;
		}
		return -1;
	}

	public boolean contains(ChatViewMessage message) {
		return indexOf(message) != -1;
	}
	
	public double getScrollValueVertical() {
		scrollPane.applyCss();
		scrollPane.layout();
		return scrollPane.getVvalue();
	}
	
	public void setScrollValueVertical(double newValue) {
		scrollPane.applyCss();
		scrollPane.layout();
		scrollPane.setVvalue(newValue);
	}
	
	public double getScrollValueHorizontal() {
		scrollPane.applyCss();
		scrollPane.layout();
		return scrollPane.getHvalue();
	}
	
	public void setScrollValueHorizontal(double newValue) {
		scrollPane.applyCss();
		scrollPane.layout();
		scrollPane.setHvalue(newValue);
	}
	
	private void initStylesheets() {
		getStyleClass().add("message-view");
		getStylesheets().add("/stylesheets/client/defaultStyle/MessageView.css");
	}
	
	private void initProperties() {
		loadingProperty = new SimpleBooleanProperty(false);
		maxMessagesProperty = new SimpleIntegerProperty(20);
		maxMessagesAtInitProperty = new SimpleIntegerProperty(getMaxMessages());
		loadValueProperty = new SimpleIntegerProperty(20);
		unloadedMessages = FXCollections.observableArrayList();
	}
	
	private void initEventHandlers() {
		scrollPane.setOnMouseEntered(a -> {
			Node bar1 = scrollPane.lookup(".scroll-bar:horizontal");
			Node bar2 = scrollPane.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(1d);
			if(bar2 != null)
				bar2.setOpacity(1d);
		});
		
		scrollPane.setOnMouseExited(b -> {
			Node bar1 = scrollPane.lookup(".scroll-bar:horizontal");
			Node bar2 = scrollPane.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(0d);
			if(bar2 != null)
				bar2.setOpacity(0d);
		});
	}
	
	private void initControls() {	
		messages = new VBox(0d);
		messages.setFillWidth(true);
		messages.setAlignment(Pos.TOP_LEFT);
		messages.setPadding(new Insets(0d));
		
		Label loadLabel = new Label("Weitere Nachrichten anzeigen");
		loadLabel.setOnMouseClicked(a -> onLoadClicked());
		loadLabel.setStyle("-fx-text-fill: white;");
		loadLabel.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 15d));

		loadButton = new HBox(10d);
		loadButton.visibleProperty().bind(Bindings.isEmpty(unloadedMessages).not());
		loadButton.setStyle("-fx-background-color: rgb(0, 0, 0, 0.7);");
		loadButton.setAlignment(Pos.CENTER);
		loadButton.setPadding(new Insets(10d));
		loadButton.getChildren().add(loadLabel);
		FXUtils.setFixedHeightOf(loadButton, 40d);
		
		messagesAndLoadButton = new StackPane();
		messagesAndLoadButton.getChildren().addAll(messages, loadButton);
		StackPane.setAlignment(loadButton, Pos.TOP_CENTER);

		contextMenu = createContextMenu();
		
		scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(messagesAndLoadButton);
	}
	
	private void initRoot() {
		setFocusTraversable(false);
		setOnContextMenuRequested(a -> onContextMenuRequested(a));
		setOnMouseClicked(b -> onMouseClicked(b));
		getChildren().add(scrollPane);
	}

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
							setStyle("-fx-background-image: url(\"" + imgFile.toURI().toURL() + "\");" + "-fx-background: rgba(64, 64, 64, 0);");
						} 
						catch (Exception e) {
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

	private ParallelTransition createAddAnimation(ChatViewMessage message, Duration duration, Interpolator interpolator) {
		ScaleTransition growAnimation = new ScaleTransition(duration, message);
		growAnimation.setFromY(0.5d);
		growAnimation.setToY(1d);

		Timeline move1 = new Timeline();
		Timeline move2 = new Timeline();
		KeyValue moveValue1 = null;
		KeyValue moveValue2 = null;

		moveValue1 = new KeyValue(message.translateXProperty(), -100d);
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
				int loading = unloadedSize < getLoadValue() ? unloadedSize : getLoadValue();
				setMaxMessages(getMaxMessages() + getLoadValue());
				for (int i = 0; i < loading; i++) {
					ChatViewMessage currentMessage = unloadedMessages.get(unloadedSize - 1);
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
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public boolean hasUnloadedMessages() {
		return !unloadedMessages.isEmpty();
	}
	
	public boolean hasMessages() {
		return !messages.getChildren().isEmpty();
	}
	
	public VBox getMessages() {
		return messages;
	}

	public HBox getLoadButton() {
		return loadButton;
	}
	
	public BooleanProperty loadingProperty() {
		return loadingProperty;
	}
	
	public boolean isLoading() {
		return loadingProperty.get();
	}
	
	public void setLoading(boolean value) {
		loadingProperty.set(value);
	}
		
	public IntegerProperty maxMessagesProperty() {
		return maxMessagesProperty;
	}
	
	public int getMaxMessages() {
		return maxMessagesProperty.get();
	}
	
	public void setMaxMessages(int value) {
		maxMessagesProperty.set(value);
	}
	
	public IntegerProperty maxMessagesAtInitProperty() {
		return maxMessagesAtInitProperty;
	}

	public int getMaxMessagesAtInit() {
		return maxMessagesAtInitProperty.get();
	}
	
	public void setMaxMessagesAtInit(int value) {
		maxMessagesAtInitProperty.set(value);
	}

	public IntegerProperty loadValueProperty() {
		return loadValueProperty;
	}
	
	public int getLoadValue() {
		return loadValueProperty.get();
	}
	
	public void setLoadValue(int value) {
		loadValueProperty.set(value);
	}

	public int getLoadedMessageCount() {
		return messages.getChildren().size();
	}
	
	public int getUnloadedMessageCount() {
		return getUnloadedMessages().size();
	}

	public ObservableList<ChatViewMessage> getUnloadedMessages() {
		return unloadedMessages;
	}
	
	public int getTotalMessageCount() {
		return getLoadedMessageCount() + getUnloadedMessageCount();
	}
}
