package gui.client.views;

import com.jfoenix.controls.JFXTabPane;

import gui.client.components.MessageView;
import gui.client.components.channelBar.ChannelBar;
import gui.client.components.channelBar.channelBarItems.MemberChannelBarItem;
import gui.client.components.channelBar.channelBarItems.TextChannelBarItem;
import gui.client.components.channelBar.channelBarItems.VoiceChannelBarItem;
import gui.client.components.clientBar.ClientBar;
import gui.client.components.messageField.MessageField;
import gui.client.components.messages.GUIMessage;
import gui.tools.GUITools;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
		this.attachStage(parentWindow);
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
		getStylesheets().add("/stylesheets/client/ClientChat.css");
	}
	
	private void initProperties() {
		minWidth = 800d;
		minHeight = 600d;
		preferredChannelBarWidth = 200d;
		preferredClientBarWidth = 200d;
	}
	
	private void initTabContainer() {
		tabPane = new JFXTabPane();
		GUITools.hideTabs(tabPane);
		
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
		GUITools.setFixedHeightOf(titleBar, 50d);
		
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
		messageView.addMessage(0, new GUIMessage("Rouman", "Jojo :A7:"));
		messageView.addMessage(1, new GUIMessage("Jenn", "Gleich ins Gym? :A2:"));
		messageView.addMessage(2, new GUIMessage("Rouman", "www.google.de Ne keinen Bock Bro sorry :A11:"));
		messageView.addMessage(3, new GUIMessage("Jenn", "Jojo fauler Spast HAUSTE REIN :B9:"));
		messageView.addMessage(4, new GUIMessage("Rouman", "Alles klar :C8:"));
		messageView.addMessage(5, new GUIMessage("Rouman", "LOL :D7:"));
		messageView.addMessage(6, new GUIMessage("Jenn", "Jojo!!!!!!!!! :B7:"));
	}
	
	private void initMessageInputField() {
		messageInputField = new MessageField();
	}
	
	public void attachStage(Stage parentWindow) {
		if(parentWindow == null)
			return;
		this.parentWindow = parentWindow;
		this.parentWindow.setMinWidth(minWidth);
		this.parentWindow.setMinHeight(minHeight);
	}
}
