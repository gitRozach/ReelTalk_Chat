package gui.views.client;

import com.jfoenix.controls.JFXTabPane;

import gui.components.MessageView;
import gui.components.channelBar.ChannelBar;
import gui.components.clientBar.ClientBar;
import gui.components.messageField.MessageField;
import gui.components.messages.GUIMessage;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.FXUtils;

public final class ClientChatView extends VBox {	
	private double minWidth;
	private double minHeight;
	private double preferredChannelBarWidth;
	private double preferredClientBarWidth;
	
	private Stage parentWindow;
	
	private JFXTabPane tabPane;
	private VBox contentRoot;
	private SplitPane splitPane;
	
	private HBox titleBar;
	private ChannelBar channelBar;
	private ClientBar clientBar;
	private MessageView messageView;
	private MessageField messageInputField;
	
	public ClientChatView() {
		this(false, null);
	}
	
	public ClientChatView(boolean initialize) {
		this(initialize, null);
	}
	
	public ClientChatView(Stage parentStage) {
		this(false, parentStage);
	}
	
	public ClientChatView(boolean initialize, Stage parentWindow) {
		super();
		if(initialize)
			initialize();
		setParentWindow(parentWindow);
	}
	
	public void initialize() {
		initStylesheets();
		initProperties();
		initMessageInputField();
		initMessageView();
		initClientBar();
		initChannelBar();
		initTitleBar();
		initSplitPane();
		initMainContainer();
		initTabContainer();
		initRoot();
	}
	
	private void initRoot() {
		setMinWidth(minWidth);
		setMinHeight(minHeight);
		setFillWidth(true);
		getChildren().add(tabPane);
		VBox.setVgrow(tabPane, Priority.ALWAYS);
	}
	
	private void initTabContainer() {
		VBox emptyBox = new VBox();
		emptyBox.setFillWidth(true);
		MediaPane mediaRoot = new MediaPane(emptyBox);
		Tab mainTab = new Tab("", contentRoot);
		Tab appTab = new Tab("", mediaRoot);
		
		tabPane = new JFXTabPane();
		tabPane.getTabs().addAll(mainTab, appTab);
		FXUtils.hideTabs(tabPane);	
	}
	
	private void initMainContainer() {
		contentRoot = new VBox();
		contentRoot.setStyle("-fx-background-color: #404040;");
		contentRoot.setFillWidth(true);
		contentRoot.getChildren().addAll(splitPane, messageInputField);
		VBox.setVgrow(splitPane, Priority.ALWAYS);
	}
	
	private void initSplitPane() {
		VBox titleBarAndMessageView = new VBox();
		titleBarAndMessageView.getChildren().addAll(titleBar, messageView);
		VBox.setVgrow(messageView, Priority.ALWAYS);
		
		splitPane = new SplitPane();
		splitPane.getStyleClass().add("root-content-box");
		splitPane.getItems().add(0, channelBar);
		splitPane.getItems().add(1, titleBarAndMessageView);
		splitPane.getItems().add(2, clientBar);
		splitPane.setDividerPositions(preferredChannelBarWidth / 1000d, 1 - (preferredClientBarWidth / 1000d));
		SplitPane.setResizableWithParent(channelBar, false);
		SplitPane.setResizableWithParent(clientBar, false);
	}
	
	private void initTitleBar() {
		HBox spacer = new HBox();
		titleBar = new HBox(spacer);
		titleBar.getStyleClass().add("title-bar");
		FXUtils.setFixedHeightOf(titleBar, 50d);
		HBox.setHgrow(spacer, Priority.SOMETIMES);
	}
	
	private void initChannelBar() {
		channelBar = new ChannelBar(true);
		channelBar.setAlignment(Pos.TOP_LEFT);
	}
	
	private void initClientBar() {
		clientBar = new ClientBar(true);
		clientBar.setAlignment(Pos.TOP_RIGHT);
	}
	
	private void initMessageView() {
		messageView = new MessageView();
	}
	
	private void initMessageInputField() {
		messageInputField = new MessageField();
	}
	
	private void initProperties() {
		minWidth = 1000d;
		minHeight = 800d;
		preferredChannelBarWidth = 200d;
		preferredClientBarWidth = 200d;
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/defaultStyle/ClientChat.css");
	}

	public VBox getContentRoot() {
		return contentRoot;
	}

	public ChannelBar getChannelBar() {
		return channelBar;
	}

	public ClientBar getClientBar() {
		return clientBar;
	}

	public MessageView getMessageView() {
		return messageView;
	}

	public MessageField getMessageInputField() {
		return messageInputField;
	}

	public void insertMessage(GUIMessage message) {
		messageView.addMessage(0, message);
	}
	
	public void appendMessage(GUIMessage message) {
		messageView.addMessage(message);
	}
	
	public void addMessage(int index, GUIMessage message) {
		messageView.addMessage(index, message);
	}
	
	public Stage getParentWindow() {
		return parentWindow;
	}
	
	public void setParentWindow(Stage window) {
		parentWindow = window;
		if(window == null)
			return;
		parentWindow.setMinWidth(minWidth);
		parentWindow.setMinHeight(minHeight);
	}
}
