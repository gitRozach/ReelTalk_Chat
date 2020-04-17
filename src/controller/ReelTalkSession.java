package controller;


import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import com.google.protobuf.Message;

import apps.audioPlayer.AudioPlayer;
import gui.components.LoadableStackPane;
import gui.components.channelBar.channelBarItems.ChannelBarChannelItem;
import gui.components.channelBar.channelBarItems.MemberChannelBarItem;
import gui.components.channelBar.channelBarItems.TextChannelBarItem;
import gui.components.messages.ChatViewMessage;
import gui.views.ChatView;
import gui.views.HostView;
import gui.views.LoginView;
import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.messages.ProtobufMessage;
import network.peer.client.ReelTalkClient;
import network.peer.server.ReelTalkServer;
import protobuf.ClientChannels.ClientChannel;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientChannelGetEvent;
import protobuf.ClientEvents.ClientChannelJoinEvent;
import protobuf.ClientEvents.ClientChannelLeaveEvent;
import protobuf.ClientEvents.ClientLoginEvent;
import protobuf.ClientEvents.ClientRequestRejectedEvent;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.wrapper.ClientRequests;
import utils.ThreadUtils;

public class ReelTalkSession extends Application {
	public static final String HOST_PROTOCOL = "TLSv1.2";
	public static final String HOST_ADDRESS = "localhost";
	public static final int HOST_PORT = 2199;
	
	private Stage window;
	private LoadableStackPane rootPane;
	
	private LoginView loginView;
	private ChatView chatView;
	private HostView hostView;
	
	private ReelTalkServer chatServer;
	private ReelTalkClient chatClient;
	
	private int currentChannelId;
	
	static final Comparator<ClientChannel> ClientChannelComparator = new Comparator<ClientChannel>() {
		@Override
		public int compare(ClientChannel o1, ClientChannel o2) {
			return o1.getBase().getChannelId() - o2.getBase().getChannelId();
		}
	};
	
	ChangeListener<? super Number> ScrollValueVerticalListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if(newValue.doubleValue() == oldValue.doubleValue())
				return;
			if(newValue.doubleValue() == 0d && !chatView.getMessageView().isLoading() && !chatView.getMessageView().hasUnloadedMessages()) {
				chatView.getMessageView().setLoading(true);
				chatClient.sendMessage(ClientRequests.newChannelMessageGetRequest(	1, 
																					chatClient.getIdentityManager().getClientUsername(), 
																					chatClient.getIdentityManager().getClientPassword(), 
																					currentChannelId, 
																					chatView.getMessageView().getTotalMessageCount() - 1, 
																					20));
			}
		}
	};
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			initialize(primaryStage);
			loadView(loginView);
			showWindow(800d, 1500d, 850d, 1000d);
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
	}
	
	public void loadView(Node view) {
		ThreadUtils.createNewThread(() -> rootPane.loadContent(view, 3000L), true);
	}
	
	public void showWindow() {
		showWindow(-1d, -1d, -1d, -1d);
	}
	
	public void showWindow(double width, double height) {
		showWindow(-1d, width, -1d, height);
	}
	
	public void showWindow(double minWidth, double showWidth, double minHeight, double showHeight) {
		setWindowMinSize(minWidth, minHeight);
		setWindowSize(showWidth, showHeight);
		Platform.runLater(() -> window.show());
		
	}
	
	public void setWindowWidth(double width) {
		Platform.runLater(() -> {
			if(width >= 0d)
			window.setWidth(width);
		});
	}
	
	public void setWindowHeight(double height) {
		Platform.runLater(() -> {
			if(height >= 0d)
				window.setHeight(height);
		});
	}
	
	public void setWindowSize(double width, double height) {
		setWindowWidth(width);
		setWindowHeight(height);
	}
	
	public void setWindowMinWidth(double minWidth) {
		Platform.runLater(() -> {
			if(minWidth >= 0d)
				window.setMinWidth(minWidth);
		});
	}
	
	public void setWindowMinHeight(double minHeight) {
		Platform.runLater(() -> {
			if(minHeight >= 0d)
				window.setMinHeight(minHeight);
		});
	}
	
	public void setWindowMinSize(double minWidth, double minHeight) {
		setWindowMinWidth(minWidth);
		setWindowMinHeight(minHeight);
	}
	
	public void closeAll() {
		try {
			if(window.isShowing())
				window.close();
			if(chatClient != null)
				chatClient.close();
			if(chatServer != null)
				chatServer.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initialize(Stage stage) throws Exception {
		initProperties();
		initLoginView();
		initChatView();
		initRootPane();
		initChatViewEventHandlers();
		initStage(stage);
		initFonts();
	}
	
	private void initProperties() {
		currentChannelId = -1;
	}
	
	private void initStage(Stage stage) {
		window = stage;
		window.setOnCloseRequest(a -> closeAll());
		window.setTitle("ReelTalk - Launcher");
		window.setScene(new Scene(rootPane, 1000d, 800d));
	}
	
	private void initFonts() {
		try {
			InputStream fontStream = getClass().getResourceAsStream("/resources/fonts/OpenSansEmoji.ttf");
			Font inputFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(inputFont);
			chatView.getMessageInputField().getTextField().getInputField().setFont(javafx.scene.text.Font.loadFont(fontStream, 15f));
		} 
		catch (FontFormatException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initRootPane() {
		rootPane = new LoadableStackPane();
		rootPane.initialize();
	}
	
	private void initLoginView() {
		loginView = new LoginView(true, window);
		loginView.getLoginButton().setOnAction(login -> onLoginButtonClicked());
		loginView.getLoginCancelButton().setOnAction(cancelLogin -> onLoginCancelButtonClicked());
		loginView.getRegisterButton().setOnAction(register -> onRegisterButtonClicked());
		loginView.getRegisterCancelButton().setOnAction(cancelRegister -> onRegisterCancelButtonClicked());
		loginView.getHostButton().setOnAction(host -> onHostButtonClicked());
		loginView.getHostCancelButton().setOnAction(cancelHost -> onHostCancelButtonClicked());
	}
	
	private void initChatView() {
		chatView = new ChatView(true, window);
	}
	
	private void initHostView() {
		hostView = new HostView();
	}
	
	private void initChatClientEventHandlers() {
		chatClient.setOnMessageReceived(new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {
				onMessageReceived(event.getAttachedObject());
			}
		});
	}
	
	private void initChatViewEventHandlers() {
		chatView.getMessageInputField().setOnEnterPressed(a -> onInputFieldEnterPressed());
		chatView.getMessageInputField().getTextField().setOnEnterPressed(b -> onInputFieldEnterPressed());
		chatView.getMessageView().getScrollPane().vvalueProperty().addListener(ScrollValueVerticalListener);				
		chatView.getClientBar().getMediaView().setOnMouseClicked(a -> {
			@SuppressWarnings("resource")
			AudioPlayer audioPlayer = new AudioPlayer();
			rootPane.showPopup(audioPlayer.getUI());
		});
		chatView.getChannelBar().setOnChannelClicked(new ObjectEventHandler<ChannelBarChannelItem>() {
			@Override
			public void handle(ObjectEvent<ChannelBarChannelItem> event) {
				onChannelBarItemClicked(event.getAttachedObject());
			}
		});
	}
	
	public void onLoginButtonClicked() {
		String address = loginView.getLoginAddressText();
		int port = Integer.parseInt(loginView.getLoginPortText());
		String username = loginView.getLoginUsernameText();
		String password = loginView.getLoginPasswordText();
		
		ThreadUtils.createNewThread(() -> {			
			rootPane.setLoading(true);
			ThreadUtils.sleep(1000L);
			try {
				if(chatClient != null)
					chatClient.close();
				chatClient = new ReelTalkClient(HOST_PROTOCOL, address, port);
				initChatClientEventHandlers();

				if(chatClient.connect())
					chatClient.sendMessage(ClientRequests.newLoginRequest(1, username, password));
				else {
					rootPane.setLoading(false);
					rootPane.showPopup("Es konnte keine Verbindung zum Server hergestellt werden.");
				}
			} 
			catch (Exception e) {
				rootPane.setLoading(false);
				rootPane.showPopup(e.getMessage());
			}
		}, true);
	}
	
	public void onLoginCancelButtonClicked() {
		closeAll();
	}
	
	public void onRegisterButtonClicked() {
		String address = loginView.getRegisterAddressText();
		int port = Integer.parseInt(loginView.getRegisterPortText());
		String username = loginView.getRegisterUsernameText();
		String password = loginView.getRegisterPasswordText();
		String passwordRepeat = loginView.getRegisterPasswordRepeatText();
		
		ThreadUtils.createNewThread(() -> {			
			rootPane.setLoading(true);
			ThreadUtils.sleep(1000L);
			try {
				if(chatClient != null)
					chatClient.close();
				chatClient = new ReelTalkClient(HOST_PROTOCOL, address, port);
				initChatClientEventHandlers();
				if(chatClient.connect())
					chatClient.sendMessage(ClientRequests.newRegistrationRequest(1, username, password, passwordRepeat, ""));
				else {
					rootPane.setLoading(false);
					rootPane.showPopup("Es konnte keine Verbindung zum Server hergestellt werden.");
				}
			} 
			catch (Exception e) {
				rootPane.setLoading(false);
				rootPane.showPopup(e.getMessage());
			}
		}, true);
	}
	
	public void onRegisterCancelButtonClicked() {
		closeAll();	
	}
	
	public void onHostButtonClicked() {
		String port = loginView.getHostPortText();
		ThreadUtils.createNewThread(() -> {			
			rootPane.setLoading(true);
			ThreadUtils.sleep(1000L);
			try {
				if(chatServer != null)
					chatServer.close();
				chatServer = new ReelTalkServer(HOST_PROTOCOL, "localhost", Integer.parseInt(port));
				chatServer.start();
				initHostView();
				loadView(hostView);
			}
			catch(Exception e) {
				rootPane.setLoading(false);
				rootPane.showPopup(e.getMessage());
			}
		}, true);
	}
	
	public void onHostCancelButtonClicked() {
		closeAll();
	}
	
	public void onChannelBarItemClicked(ChannelBarChannelItem item) {
		if(item == null)
			return;
		int channelId = item.getChannelId();
		if(channelId != currentChannelId) {
			String username = chatClient.getIdentityManager().getClientUsername();
			String password = chatClient.getIdentityManager().getClientPassword();
			
			chatClient.sendMessage(ClientRequests.newChannelLeaveRequest(1, username, password, currentChannelId));
			chatView.getMessageView().clear();
			chatView.getMessageView().setLoading(true);
			chatClient.sendMessage(ClientRequests.newChannelJoinRequest(1, username, password, channelId));
		}
	}
	
	public void onInputFieldEnterPressed() {
		ChannelMessagePostRequest request = ClientRequests.newChannelMessagePostRequest(1, chatClient.getIdentityManager().getClientUsername(), chatClient.getIdentityManager().getClientPassword(), 1, chatView.getMessageInputField().getText());
		chatClient.sendMessage(request);
		chatView.getMessageInputField().getTextField().clear();
	}
	
	public void onMessageReceived(ProtobufMessage reception) {
		if(reception == null || !reception.hasMessage())
			return;
		Message message = reception.getMessage();
		if(message instanceof ClientRequestRejectedEvent)
			onClientRequestRejectedEvent((ClientRequestRejectedEvent) message);
		else if(message instanceof ClientLoginEvent)
			onClientLoginEvent((ClientLoginEvent) message);
		else if(message instanceof ChannelMessagePostEvent)
			onChannelMessagePostEvent((ChannelMessagePostEvent) message);
		else if(message instanceof ChannelMessageGetEvent)
			onChannelMessageGetEvent((ChannelMessageGetEvent) message);
		else if(message instanceof ClientChannelJoinEvent)
			onClientChannelJoinEvent((ClientChannelJoinEvent) message);
		else if(message instanceof ClientChannelLeaveEvent)
			onClientChannelLeaveEvent((ClientChannelLeaveEvent) message);
		else if(message instanceof ClientChannelGetEvent)
			onClientChannelGetEvent((ClientChannelGetEvent) message);
	}
	
	public void onClientRequestRejectedEvent(ClientRequestRejectedEvent event) {
		rootPane.setLoading(false);
		rootPane.showPopup(event.getRejectionMessage());
	}
	
	public void onClientLoginEvent(ClientLoginEvent event) {
		chatView.getClientBar().setProfileName(event.getAccount().getProfile().getBase().getUsername());
		for(ClientChannel currentChannel : event.getServerChannelList())
			chatView.getChannelBar().addChannel(new TextChannelBarItem(currentChannel.getBase().getChannelId(), currentChannel.getBase().getChannelName()));
		for(ClientProfile currentProfile : event.getMemberProfileList()) 
			chatView.getClientBar().addMemberItem(currentProfile.getBase().getId(), currentProfile.getBase().getUsername());
		loadView(chatView);
		setWindowMinSize(1000d, 900d);
	}
	
	public void onChannelMessagePostEvent(ChannelMessagePostEvent event) {
		for(ChannelMessage currentMessage : event.getMessageList()) {
			ChatViewMessage newMessage = new ChatViewMessage();
			newMessage.setSender(currentMessage.getMessageBase().getSenderUsername());
			newMessage.setMessage(currentMessage.getMessageBase().getMessageText());
			newMessage.setTime(currentMessage.getMessageBase().getTimestampMillis());
			chatView.getMessageView().addMessageAnimated(newMessage);
		}
	}
	
	public void onChannelMessageGetEvent(ChannelMessageGetEvent event) {
		for(int i = event.getMessageCount() - 1; i >= 0; --i) {
			ChannelMessage currentMessage = event.getMessage(i);
			ChatViewMessage newMessage = new ChatViewMessage();
			newMessage.setSender(currentMessage.getMessageBase().getSenderUsername());
			newMessage.setMessage(currentMessage.getMessageBase().getMessageText());
			newMessage.setTime(currentMessage.getMessageBase().getTimestampMillis());
			chatView.getMessageView().addMessage(0, newMessage);
		}
		chatView.getMessageView().setLoading(false);
	}
	
	public void onClientChannelJoinEvent(ClientChannelJoinEvent event) {
		int channelId = event.getChannelBase().getChannelId();
		int clientId = chatClient.getIdentityManager().getClientId();
		String clientUsername = chatClient.getIdentityManager().getClientUsername();
		
		currentChannelId = channelId;
				
		if(!chatView.getChannelBar().channelContainsClientWithId(channelId, clientId))
			chatView.getChannelBar().addClient(channelId, new MemberChannelBarItem(clientId, clientUsername));
		
		for(ChannelMessage channelMessage : event.getChannelMessageList()) {
			ChatViewMessage newMessage = new ChatViewMessage();
			newMessage.setSender(channelMessage.getMessageBase().getSenderUsername());
			newMessage.setMessage(channelMessage.getMessageBase().getMessageText());
			newMessage.setTime(channelMessage.getMessageBase().getTimestampMillis());
			chatView.getMessageView().addMessage(newMessage);
		}
		chatView.getMessageView().setLoading(false);
	}
	
	public void onClientChannelLeaveEvent(ClientChannelLeaveEvent event) {
		int channelId = event.getChannelBase().getChannelId();
		int clientId = event.getEventBase().getRequestorClientBase().getId();
		if(chatView.getChannelBar().channelContainsClientWithId(channelId, clientId))
			chatView.getChannelBar().removeClientFromChannel(clientId, channelId);
	}
	
	public void onClientChannelGetEvent(ClientChannelGetEvent event) {
		for(ClientChannel currentChannel : event.getChannelList())
			chatView.getChannelBar().addChannel(new TextChannelBarItem(currentChannel.getBase().getChannelId(), currentChannel.getBase().getChannelName()));
	}
}
