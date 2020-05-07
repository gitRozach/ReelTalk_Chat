package controller;


import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import com.google.protobuf.Message;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;

import apps.audioPlayer.AudioPlayer;
import gui.components.LoadableStackPane;
import gui.components.channelBar.channelBarItems.ChannelBarChannelItem;
import gui.components.channelBar.channelBarItems.MemberChannelBarItem;
import gui.components.channelBar.channelBarItems.TextChannelBarItem;
import gui.components.clientBar.items.ClientBarMemberItem;
import gui.components.clientBar.items.MemberClientBarItem;
import gui.components.messages.ChatViewMessage;
import gui.views.ChatView;
import gui.views.HostView;
import gui.views.LoginView;
import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import network.messages.ProtobufMessage;
import network.peer.client.ReelTalkClient;
import network.peer.server.ReelTalkServer;
import protobuf.ClientChannels.Channel;
import protobuf.ClientEvents.ChannelGetEvent;
import protobuf.ClientEvents.ChannelJoinEvent;
import protobuf.ClientEvents.ChannelLeaveEvent;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientJoinedChannelEvent;
import protobuf.ClientEvents.ClientLeftChannelEvent;
import protobuf.ClientEvents.ClientLoggedInEvent;
import protobuf.ClientEvents.ClientLoggedOutEvent;
import protobuf.ClientEvents.LoginEvent;
import protobuf.ClientEvents.RequestRejectedEvent;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.wrapper.ClientRequests;
import utils.ThreadUtils;

public class ReelTalkSession extends Application {
	public static final String HOST_PROTOCOL = "TLSv1.2";
	public static final String HOST_ADDRESS = "localhost";
	public static final int HOST_PORT = 2199;
	
	private BooleanProperty loginControlsEnabledProperty;
	private BooleanProperty registerControlsEnabledProperty;
	private BooleanProperty hostControlsEnabledProperty;
	private volatile int currentChannelId;
	
	private Stage window;
	private LoadableStackPane rootPane;
	
	private LoginView loginView;
	private ChatView chatView;
	private HostView hostView;
	
	private ReelTalkServer chatServer;
	private ReelTalkClient chatClient;
	
	static final Comparator<Channel> ClientChannelComparator = new Comparator<Channel>() {
		@Override
		public int compare(Channel o1, Channel o2) {
			return o1.getBase().getId() - o2.getBase().getId();
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
				chatClient.disconnect();
			if(chatServer != null) {
				chatServer.stop();
				chatServer.close();
			}
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
		loginControlsEnabledProperty = new SimpleBooleanProperty(true);
		registerControlsEnabledProperty = new SimpleBooleanProperty(true);
		hostControlsEnabledProperty = new SimpleBooleanProperty(true);
		currentChannelId = -1;
	}
	
	private void initStage(Stage stage) {
		window = stage;
		window.setOnCloseRequest(a -> closeAll());
		window.setTitle("ReelTalk - Launcher");
		
		JFXDecorator decoratedWindow = new JFXDecorator(window, rootPane);
		decoratedWindow.getStylesheets().add("/stylesheets/client/defaultStyle/Window.css");
		decoratedWindow.setGraphic(new Label("Icon"));
		decoratedWindow.setCustomMaximize(false);
		
		window.setScene(new Scene(decoratedWindow, 1000d, 800d));
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
		loginView.getLoginContainer().disableProperty().bind(loginControlsEnabledProperty.not());
		loginView.getLoginContainer().addEventFilter(KeyEvent.KEY_PRESSED, l -> {
			if(l.getCode() == KeyCode.ENTER) {
				onLoginButtonClicked(); 
				l.consume();
			}
		});
		loginView.getRegisterContainer().disableProperty().bind(registerControlsEnabledProperty.not());
		loginView.getRegisterContainer().addEventFilter(KeyEvent.KEY_PRESSED, r -> {
			if(r.getCode() == KeyCode.ENTER) {
				onRegisterButtonClicked(); 
				r.consume();
			}
		});
		loginView.getHostContainer().disableProperty().bind(hostControlsEnabledProperty.not());
		loginView.getHostContainer().addEventFilter(KeyEvent.KEY_PRESSED, h -> {
			if(h.getCode() == KeyCode.ENTER) {
				onHostButtonClicked(); 
				h.consume();
			}
		});
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
		ThreadUtils.createNewThread(() -> {
			String address = loginView.getLoginAddressText();
			String port = loginView.getLoginPortText();
			String username = loginView.getLoginUsernameText();
			String password = loginView.getLoginPasswordText();
			
			setLoginControlsEnabled(false);
			rootPane.setLoading(true);
			ThreadUtils.sleep(1000L);
			try {
				if(chatClient != null)
					chatClient.close();
				chatClient = new ReelTalkClient(HOST_PROTOCOL, address, Integer.parseInt(port));
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
			finally {
				setLoginControlsEnabled(true);
			}
		}, true);
	}
	
	public void onLoginCancelButtonClicked() {
		closeAll();
	}
	
	public void onRegisterButtonClicked() {
		ThreadUtils.createNewThread(() -> {
			String address = loginView.getRegisterAddressText();
			String port = loginView.getRegisterPortText();
			String username = loginView.getRegisterUsernameText();
			String password = loginView.getRegisterPasswordText();
			String passwordRepeat = loginView.getRegisterPasswordRepeatText();
			
			setRegisterControlsEnabled(false);
			rootPane.setLoading(true);
			ThreadUtils.sleep(1000L);
			try {
				if(chatClient != null)
					chatClient.close();
				chatClient = new ReelTalkClient(HOST_PROTOCOL, address, Integer.parseInt(port));
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
			finally {
				setRegisterControlsEnabled(true);
			}
		}, true);
	}
	
	public void onRegisterCancelButtonClicked() {
		closeAll();	
	}
	
	public void onHostButtonClicked() {
		ThreadUtils.createNewThread(() -> {
			String port = loginView.getHostPortText();
			
			setHostControlsEnabled(false);
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
			finally {
				setHostControlsEnabled(true);
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
		if(message instanceof RequestRejectedEvent)
			onRequestRejectedEvent((RequestRejectedEvent) message);
		else if(message instanceof LoginEvent)
			onLoginEvent((LoginEvent) message);
		else if(message instanceof ClientLoggedInEvent)
			onClientLoggedInEvent((ClientLoggedInEvent) message);
		else if(message instanceof ClientLoggedOutEvent)
			onClientLoggedOutEvent((ClientLoggedOutEvent) message);
		else if(message instanceof ChannelMessagePostEvent)
			onChannelMessagePostEvent((ChannelMessagePostEvent) message);
		else if(message instanceof ChannelMessageGetEvent)
			onChannelMessageGetEvent((ChannelMessageGetEvent) message);
		else if(message instanceof ChannelJoinEvent)
			onChannelJoinEvent((ChannelJoinEvent) message);
		else if(message instanceof ChannelLeaveEvent)
			onChannelLeaveEvent((ChannelLeaveEvent) message);
		else if(message instanceof ClientJoinedChannelEvent)
			onClientJoinedChannelEvent((ClientJoinedChannelEvent) message);
		else if(message instanceof ClientLeftChannelEvent)
			onClientLeftChannelEvent((ClientLeftChannelEvent) message);
		else if(message instanceof ChannelGetEvent)
			onChannelGetEvent((ChannelGetEvent) message);
	}
	
	public void onRequestRejectedEvent(RequestRejectedEvent event) {
		rootPane.setLoading(false);
		rootPane.showPopup(event.getRejectionMessage());
	}
	
	public void onLoginEvent(LoginEvent event) {
		chatView.getClientBar().setProfileName(event.getAccount().getProfile().getBase().getUsername());
		for(Channel currentChannel : event.getServerChannelList()) {
			int channelId = currentChannel.getBase().getId();
		
			chatView.getChannelBar().addChannel(new TextChannelBarItem(channelId, currentChannel.getBase().getName()));
			for(Integer memberId : currentChannel.getMemberIdList()) {
				ClientProfile memberProfile = chatClient.getProfileManager().getClientProfileById(memberId);
				if(!chatView.getChannelBar().channelContainsClientWithId(channelId, memberId))
					chatView.getChannelBar().addClient(channelId, new MemberChannelBarItem(memberId, memberProfile.getBase().getUsername()));
			}
		}
		for(ClientProfile currentProfile : event.getMemberProfileList()) 
			chatView.getClientBar().addMemberItem(currentProfile.getBase().getId(), currentProfile.getBase().getUsername());
		loadView(chatView);
		setWindowMinSize(1000d, 900d);
	}
	
	public void onClientLoggedInEvent(ClientLoggedInEvent event) {
		int clientId = event.getClientBase().getId();
		String clientUsername = event.getClientBase().getUsername();
		ClientStatus clientStatus = ClientStatus.ONLINE;
		
		JFXListView<ClientBarMemberItem> memberList = chatView.getClientBar().getNonFriendsView();
		for(int i = 0; i < memberList.getItems().size(); ++i) {
			if(memberList.getItems().get(i).getId() == clientId) {
				ClientBarMemberItem newItem = new MemberClientBarItem(clientId, clientUsername);
				newItem.setClientStatus(clientStatus);
				chatView.getClientBar().getNonFriendsView().getItems().set(i, newItem);
				return;
			}
		}
	}
	
	public void onClientLoggedOutEvent(ClientLoggedOutEvent event) {
		int clientId = event.getClientBase().getId();
		String clientUsername = event.getClientBase().getUsername();
		ClientStatus clientStatus = ClientStatus.OFFLINE;
		
		//STATTDESSEN:
		//ClientStatusChangedEvent schicken und den ClientStatus vom Server mit dem des Clients synchronisieren
		
		JFXListView<ClientBarMemberItem> memberList = chatView.getClientBar().getNonFriendsView();
		for(int i = 0; i < memberList.getItems().size(); ++i) {
			if(memberList.getItems().get(i).getId() == clientId) {
				ClientBarMemberItem newItem = new MemberClientBarItem(clientId, clientUsername);
				newItem.setClientStatus(clientStatus);
				chatView.getClientBar().getNonFriendsView().getItems().set(i, newItem);
				return;
			}
		}
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
	
	public void onChannelJoinEvent(ChannelJoinEvent event) {
		int channelId = event.getChannelBase().getId();
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
	
	public void onChannelLeaveEvent(ChannelLeaveEvent event) {
		int channelId = event.getChannelBase().getId();
		int clientId = event.getProfile().getBase().getId();
		if(chatView.getChannelBar().channelContainsClientWithId(channelId, clientId))
			chatView.getChannelBar().removeClientFromChannel(clientId, channelId);
	}
	
	public void onClientJoinedChannelEvent(ClientJoinedChannelEvent event) {
		int channelId = event.getChannelBase().getId();
		int clientId = event.getClientBase().getId();
		String clientUsername = event.getClientBase().getUsername();
		if(!chatView.getChannelBar().channelContainsClientWithId(channelId, clientId))
			chatView.getChannelBar().addClient(channelId, new MemberChannelBarItem(clientId, clientUsername));
	}
	
	public void onClientLeftChannelEvent(ClientLeftChannelEvent event) {
		int channelId = event.getChannelBase().getId();
		int clientId = event.getClientBase().getId();
		if(chatView.getChannelBar().channelContainsClientWithId(channelId, clientId))
			chatView.getChannelBar().removeClientFromChannel(clientId, channelId);
	}
	
	public void onChannelGetEvent(ChannelGetEvent event) {
		for(Channel currentChannel : event.getChannelList())
			chatView.getChannelBar().addChannel(new TextChannelBarItem(currentChannel.getBase().getId(), currentChannel.getBase().getName()));
	}
	
	public BooleanProperty loginControlsEnabledProperty() {
		return loginControlsEnabledProperty;
	}
	
	public boolean isLoginControlsEnabled() {
		return loginControlsEnabledProperty.get();
	}
	
	public void setLoginControlsEnabled(boolean value) {
		loginControlsEnabledProperty.set(value);
	}
	
	public BooleanProperty registerControlsEnabledProperty() {
		return registerControlsEnabledProperty;
	}
	
	public boolean isRegisterControlsEnabled() {
		return registerControlsEnabledProperty.get();
	}
	
	public void setRegisterControlsEnabled(boolean value) {
		registerControlsEnabledProperty.set(value);
	}
	
	public BooleanProperty hostControlsEnabledProperty() {
		return hostControlsEnabledProperty;
	}
	
	public boolean isHostControlsEnabled() {
		return hostControlsEnabledProperty.get();
	}
	
	public void setHostControlsEnabled(boolean value) {
		hostControlsEnabledProperty.set(value);
	}
}
