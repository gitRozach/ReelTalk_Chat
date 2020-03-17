package gui.views.client;

import com.jfoenix.controls.JFXTabPane;

import gui.components.MessageView;
import gui.components.channelBar.ChannelBar;
import gui.components.channelBar.channelBarItems.MemberChannelBarItem;
import gui.components.channelBar.channelBarItems.TextChannelBarItem;
import gui.components.channelBar.channelBarItems.VoiceChannelBarItem;
import gui.components.clientBar.ClientBar;
import gui.components.messageField.MessageField;
import gui.components.messages.GUIMessage;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.JFXUtils;

public final class ClientChatView extends StackPane {	
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
		this(false);
	}
	
	public ClientChatView(boolean initialize) {
		this(initialize, null);
	}
	
	public ClientChatView(boolean initialize, Stage parentWindow) {
		super();
		
		if(initialize)
			initialize();
		setStage(parentWindow);
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
		
		setMinWidth(minWidth);
		setMinHeight(minHeight);
		//loadContent(tabPane);
		getChildren().add(tabPane);
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/defaultStyle/ClientChat.css");
	}
	
	private void initProperties() {
		minWidth = 800d;
		minHeight = 600d;
		preferredChannelBarWidth = 200d;
		preferredClientBarWidth = 200d;
	}
	
	private void initTabContainer() {
		tabPane = new JFXTabPane();
		JFXUtils.hideTabs(tabPane);
		
		VBox emptyBox = new VBox();
		emptyBox.setFillWidth(true);
		MediaPane mediaRoot = new MediaPane(emptyBox);
		
		Tab mainTab = new Tab("", contentRoot);
		Tab appTab = new Tab("", mediaRoot);
		
		tabPane.getTabs().addAll(mainTab, appTab);
	}
	
	private void initMainContainer() {
		contentRoot = new VBox();
		contentRoot.setStyle("-fx-background-color: #404040;");
		contentRoot.setFillWidth(true);
		contentRoot.getChildren().addAll(splitPane, messageInputField);
		VBox.setVgrow(splitPane, Priority.ALWAYS);
	}
	
	private void initSplitPane() {
		splitPane = new SplitPane();
		splitPane.getStyleClass().add("root-content-box");
		
		VBox titleBarAndMessageView = new VBox();
		titleBarAndMessageView.getChildren().addAll(titleBar, messageView);
		VBox.setVgrow(messageView, Priority.ALWAYS);

		//primarySplitPane.getItems().add(0, titleBarAndSecondarySplitPane);
		splitPane.getItems().add(0, channelBar);
		splitPane.getItems().add(1, titleBarAndMessageView);
		splitPane.getItems().add(2, clientBar);
		splitPane.setDividerPositions(preferredChannelBarWidth / 1000d, 1 - (preferredClientBarWidth / 1000d));
		SplitPane.setResizableWithParent(channelBar, false);
		SplitPane.setResizableWithParent(clientBar, false);
		
//		primarySplitPane.getDividers().get(0).positionProperty().addListener((obs, oldV, newV) -> {
//			preferredClientBarWidth = getWidth() * (1 - newV.doubleValue());
//		});
	}
	
	private void initTitleBar() {
		titleBar = new HBox();
		titleBar.getStyleClass().add("title-bar");
		JFXUtils.setFixedHeightOf(titleBar, 50d);
		
		HBox spacer = new HBox();
		
		titleBar.getChildren().addAll(spacer);
		HBox.setHgrow(spacer, Priority.SOMETIMES);
	}
	
	private void initChannelBar() {
		channelBar = new ChannelBar(true);
		channelBar.setAlignment(Pos.TOP_LEFT);
		
		TextChannelBarItem textC1 = new TextChannelBarItem(1, "Text Channel 1");
		VoiceChannelBarItem voiceC1 = new VoiceChannelBarItem(2, "Voice Channel 1");
		VoiceChannelBarItem voiceC2 = new VoiceChannelBarItem(3, "Voice Channel 2");
		
		MemberChannelBarItem mem1 = new MemberChannelBarItem(1, "Rozach");
		MemberChannelBarItem mem2 = new MemberChannelBarItem(2, "Jenn");
		MemberChannelBarItem mem3 = new MemberChannelBarItem(3, "Max");
		MemberChannelBarItem mem4 = new MemberChannelBarItem(4, "Hendrik");
		MemberChannelBarItem mem5 = new MemberChannelBarItem(5, "Husseini");
		MemberChannelBarItem mem6 = new MemberChannelBarItem(6, "Peter");
		MemberChannelBarItem mem7 = new MemberChannelBarItem(7, "Michael");
		MemberChannelBarItem mem8 = new MemberChannelBarItem(8, "Moustafa");
		MemberChannelBarItem mem9 = new MemberChannelBarItem(9, "Osama");
		MemberChannelBarItem mem10 = new MemberChannelBarItem(10, "Nasya");
		
		channelBar.addChannel(textC1);
		channelBar.addChannel(voiceC1);
		channelBar.addChannel(voiceC2);
		
		channelBar.addClient(1, mem1);
		channelBar.addClient(1, mem2);
		channelBar.addClient(1, mem3);
		channelBar.addClient(1, mem4);
		channelBar.addClient(1, mem5);
		
		channelBar.addClient(2, mem6);
		channelBar.addClient(2, mem7);
		
		channelBar.addClient(3, mem8);
		channelBar.addClient(3, mem9);
		channelBar.addClient(3, mem10);
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
	
	public Stage getParentWindow() {
		return parentWindow;
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
	
	public void setStage(Stage parentWindow) {
		if(parentWindow == null)
			return;
		this.parentWindow = parentWindow;
		this.parentWindow.setMinWidth(minWidth);
		this.parentWindow.setMinHeight(minHeight);
	}
}