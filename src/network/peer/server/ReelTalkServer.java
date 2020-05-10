package network.peer.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.protobuf.Message;

import database.protobuf.server.ChannelMessageDatabase;
import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import network.messages.ProtobufMessage;
import network.peer.callbacks.PeerCallback;
import network.peer.manager.ReelTalkChannelManager;
import network.peer.manager.ReelTalkClientProfileManager;
import network.peer.manager.ReelTalkConfigurationManager;
import network.peer.server.manager.ReelTalkServerDatabaseManager;
import protobuf.ClientChannels.Channel;
import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientEvents.ChannelJoinEvent;
import protobuf.ClientEvents.ChannelLeaveEvent;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientJoinedChannelEvent;
import protobuf.ClientEvents.ClientLeftChannelEvent;
import protobuf.ClientEvents.ClientLoggedInEvent;
import protobuf.ClientEvents.EventBase;
import protobuf.ClientEvents.LoginEvent;
import protobuf.ClientEvents.RequestRejectedEvent;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBadges;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientFriends;
import protobuf.ClientIdentities.ClientGroups;
import protobuf.ClientIdentities.ClientImages;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.FileMessageBase;
import protobuf.ClientRequests.ChannelFileDownloadRequest;
import protobuf.ClientRequests.ChannelFileUploadRequest;
import protobuf.ClientRequests.ChannelJoinRequest;
import protobuf.ClientRequests.ChannelLeaveRequest;
import protobuf.ClientRequests.ChannelMessageGetRequest;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.ClientRequests.LoginRequest;
import protobuf.ClientRequests.LogoutRequest;
import protobuf.ClientRequests.PingMeasurementRequest;
import protobuf.ClientRequests.PrivateFileDownloadRequest;
import protobuf.ClientRequests.PrivateFileUploadRequest;
import protobuf.ClientRequests.PrivateMessageGetRequest;
import protobuf.ClientRequests.PrivateMessagePostRequest;
import protobuf.ClientRequests.ProfileGetRequest;
import protobuf.ClientRequests.RegistrationRequest;
import protobuf.wrapper.ClientChannels;
import protobuf.wrapper.ClientEvents;
import protobuf.wrapper.ClientIdentities;
import protobuf.wrapper.ClientMessages;
import protobuf.wrapper.ClientRequests;

public class ReelTalkServer extends SecuredProtobufServer {	
	protected ReelTalkConfigurationManager configManager;
	protected ReelTalkServerDatabaseManager databaseManager;
	protected ReelTalkClientProfileManager clientManager;
	protected ReelTalkChannelManager channelManager;
	
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageTimedOutHandler;
	protected ObjectEventHandler<Throwable> onConnectionLostHandler;

	public ReelTalkServer(String protocol, String hostAddress, int port) throws Exception {
		super(protocol, hostAddress, port);
		initConfigurationManager();
		initDatabaseManager();
		initClientManager();
		initChannelManager();
		initHandlers();
		initCallbacks();
		logger.info(databaseManager.getClientAccountDatabase().size() + " clients loaded.");
		logger.info(databaseManager.getClientChannelDatabase().size() + " channels loaded.");
		logger.info(databaseManager.getChannelMessageDatabase().size() + " channel messages loaded.");
		logger.info(databaseManager.getPrivateMessageDatabase().size() + " private messages loaded.");
		logger.info(databaseManager.getProfileCommentDatabase().size() + " profile comments loaded.");
	}
	
	public int sendMessageToChannel(int channelId, Message message) {
		int sendMessagesCounter = 0;
		
		return sendMessagesCounter;
	}
	
	public boolean sendMessageToClient(int clientId, Message message) {
		return true;
	}
	
	public void sendMessageToAllClients(Message message) {
		
	}
	
	public void sendMessageToAllClientsExcept(int clientId, Message message) {
		
	}
	
	public boolean checkLogin(String username, String password) {
		return login(username, password) != null;
	}
	
	public ClientAccount login(String username, String password) {
		List<ClientAccount> accounts = databaseManager.getClientAccountDatabase().getByUsernameAndPassword(username, password);
		return accounts.isEmpty() ? null : accounts.get(accounts.size() - 1);
	}
	
	private void initDatabaseManager() throws IOException {
		databaseManager = new ReelTalkServerDatabaseManager();
		databaseManager.getClientAccountDatabase().loadFileItems("src/clientData/accounts.txt");
		databaseManager.getClientChannelDatabase().loadFileItems("src/clientData/channels.txt");
	}
	
	private void initConfigurationManager() {
		configManager = new ReelTalkConfigurationManager();
		
	}
	
	private void initClientManager() {
		clientManager = new ReelTalkClientProfileManager();
		for(ClientAccount currentAccount : databaseManager.getClientAccountDatabase().getItems())
			clientManager.addClient(currentAccount.getProfile());
	}
	
	private void initChannelManager() {
		channelManager = new ReelTalkChannelManager();
		channelManager.addChannels(databaseManager.getClientChannelDatabase().getItems());			
	}
	
	private void initHandlers() {
		onMessageReceivedHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {}
		};
		onMessageSentHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {}
		};
		onMessageTimedOutHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {}
		};
		onConnectionLostHandler = new ObjectEventHandler<Throwable>() {
			@Override
			public void handle(ObjectEvent<Throwable> event) {}
		};
	}
	
	private void initCallbacks() {
		setPeerCallback(new PeerCallback() {
			@Override
			public void messageReceived(ProtobufMessage message) {
				handleReceivedMessage(message);
			}
			@Override
			public void messageSent(ProtobufMessage message) {
				handleSentMessage(message);
			}
			@Override
			public void messageTimedOut(ProtobufMessage message) {
				handleTimedOutMessage(message);
			}
			@Override
			public void connectionLost(Throwable throwable) {
				handleLostConnection(throwable);
			}
		});
	}
	
	private void handleReceivedMessage(ProtobufMessage receivedMessage) {
		if(!receivedMessage.hasSocketChannel() || !receivedMessage.hasMessage())
			return;
		if(ClientRequests.isRequest(receivedMessage.getMessage().getClass()))
			handleRequest(getLocalSelectionKey(receivedMessage.getSocketChannel()), receivedMessage.getMessage());
		onMessageReceivedHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, receivedMessage));	
	}
	
	private void handleSentMessage(ProtobufMessage sentMessage) {
		onMessageSentHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, sentMessage));
	}
	
	private void handleTimedOutMessage(ProtobufMessage timedOutMessage) {
		onMessageTimedOutHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, timedOutMessage));	
	}
	
	private void handleLostConnection(Throwable throwable) {
		onConnectionLostHandler.handle(new ObjectEvent<Throwable>(ObjectEvent.ANY, throwable));
	}
	
	private void handleRequest(SelectionKey clientKey, Message request) {
		if(clientKey == null || request == null)
			return;
		switch(request.getClass().getSimpleName()) {
		case "ChannelMessageGetRequest":
			handleChannelMessageGetRequest(clientKey, (ChannelMessageGetRequest)request);
			break;
		case "ChannelMessagePostRequest":
			handleChannelMessagePostRequest(clientKey, (ChannelMessagePostRequest)request);
			break;
		case "PrivateMessageGetRequest":
			handlePrivateMessageGetRequest(clientKey, (PrivateMessageGetRequest)request);
			break;
		case "PrivateMessagePostRequest":
			handlePrivateMessagePostRequest(clientKey, (PrivateMessagePostRequest)request);
			break;
		case "LoginRequest":
			handleLoginRequest(clientKey, (LoginRequest)request);
			break;
		case "LogoutRequest":
			handleLogoutRequest(clientKey, (LogoutRequest)request);
			break;
		case "RegistrationRequest":
			handleRegistrationRequest(clientKey, (RegistrationRequest)request);
			break;
		case "ChannelFileDownloadRequest":
			handleChannelFileDownloadRequest(clientKey, (ChannelFileDownloadRequest)request);
			break;
		case "ChannelFileUploadRequest":
			handleChannelFileUploadRequest(clientKey, (ChannelFileUploadRequest)request);
			break;
		case "PrivateFileDownloadRequest":
			handlePrivateFileDownloadRequest(clientKey, (PrivateFileDownloadRequest)request);
			break;
		case "PrivateFileUploadRequest":
			handlePrivateFileUploadRequest(clientKey, (PrivateFileUploadRequest)request);
			break;
		case "PingMeasurementRequest":
			handlePingMeasurementRequest(clientKey, (PingMeasurementRequest)request);
			break;
		case "ProfileGetRequest":
			handleProfileGetRequest(clientKey, (ProfileGetRequest)request);
			break;
		case "ClientChannelRequest":
			break;
		case "ChannelJoinRequest":
			handleChannelJoinRequest(clientKey, (ChannelJoinRequest)request);
			break;
		case "ChannelLeaveRequest":
			handleChannelLeaveRequest(clientKey, (ChannelLeaveRequest)request);
			break;
		}
	}
	
	private void handleChannelJoinRequest(SelectionKey clientKey, ChannelJoinRequest request) {
		if(request == null)
			return;
		int channelId = request.getChannelBase().getId();
		String channelName = request.getChannelBase().getName();
		String clientUsername = request.getRequestBase().getUsername();
		String clientPassword = request.getRequestBase().getPassword();
		ClientAccount clientAccount = login(clientUsername, clientPassword);
		
		if(clientAccount != null) {
			channelManager.addClientToChannel(channelId, clientAccount.getProfile().getBase().getId());
			ChannelMessageDatabase messageDb = databaseManager.getChannelMessageDatabase();
			messageDb.loadFileItems("src/clientData/channelMessages/" + messageDb.createChannelMessageFileNameFromId(channelId));
			List<ChannelMessage> channelMessages = messageDb.getChannelMessages(20);
			if(channelMessages == null)
				channelMessages = Collections.emptyList();
			ChannelJoinEvent joinEvent = ClientEvents.newChannelJoinEvent(1, channelId, channelMessages);
			sendMessage(clientKey, joinEvent);
			
			int clientId = clientAccount.getProfile().getBase().getId();
			EventBase eventBase = ClientEvents.newEventBase(1, clientId);
			ChannelBase channelBase = ClientChannels.newChannelBase(channelId, channelName);
			ClientBase clientBase = ClientIdentities.newClientBase(clientId, clientUsername);
			ClientJoinedChannelEvent leftEvent = ClientJoinedChannelEvent.newBuilder().setEventBase(eventBase).setChannelBase(channelBase).setClientBase(clientBase).build();
			for(SelectionKey key : selector.keys())
				if(key.channel() instanceof SocketChannel)
					sendMessage(key, leftEvent);
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelLeaveRequest(SelectionKey clientKey, ChannelLeaveRequest request) {
		if(request == null)
			return;
		int channelId = request.getChannelBase().getId();
		String channelName = request.getChannelBase().getName();
		String clientUsername = request.getRequestBase().getUsername();
		String clientPassword = request.getRequestBase().getPassword();
		ClientAccount clientAccount = login(clientUsername, clientPassword);
		
		if(clientAccount != null) {
			System.out.println("Channel leave result: " + channelManager.removeClientFromChannel(clientAccount.getProfile().getBase().getId(), channelId));
			ChannelLeaveEvent leaveEvent = ClientEvents.newChannelLeaveEvent(1, channelId, clientAccount.getProfile());
			sendMessage(clientKey, leaveEvent);
			
			int clientId = clientAccount.getProfile().getBase().getId();
			EventBase eventBase = ClientEvents.newEventBase(1, clientId);
			ChannelBase channelBase = ClientChannels.newChannelBase(channelId, channelName);
			ClientBase clientBase = ClientIdentities.newClientBase(clientId, clientUsername);
			ClientLeftChannelEvent leftEvent = ClientLeftChannelEvent.newBuilder().setEventBase(eventBase).setChannelBase(channelBase).setClientBase(clientBase).build();
			for(SelectionKey key : selector.keys())
				if(key.channel() instanceof SocketChannel)
					sendMessage(key, leftEvent);
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelMessageGetRequest(SelectionKey clientKey, ChannelMessageGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			if(request.getLastIndex() >= databaseManager.getChannelMessageDatabase().size() - 1)
				return;
			int startIndex = (databaseManager.getChannelMessageDatabase().size() - request.getLastIndex() - 2 - request.getMessageCount())  < 0 ? 0 : (databaseManager.getChannelMessageDatabase().size() - request.getLastIndex() - 2 - request.getMessageCount()); 
			int endIndex = databaseManager.getChannelMessageDatabase().size() - request.getLastIndex() - 2;
			System.out.println("Start: " + startIndex + " - End: " + endIndex);
			if(startIndex < 0 || startIndex > endIndex)
				return;
			ChannelMessageGetEvent channelMessage = ClientEvents.newChannelMessageGetEvent(1, 1, databaseManager.getChannelMessageDatabase().getItems(startIndex, endIndex));
			sendMessage(clientKey, channelMessage);
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelMessagePostRequest(SelectionKey clientKey, ChannelMessagePostRequest request) {
		String clientUsername = request.getRequestBase().getUsername();
		String clientPassword = request.getRequestBase().getPassword();
		int messageId = 1;
		String messageText = request.getMessageText();
		long messageTime = System.currentTimeMillis();
		int channelId = request.getChannelBase().getId();
		int eventId = request.getRequestBase().getRequestId();
		Collection<FileMessageBase> attachedFiles = Collections.emptyList();
		ClientAccount clientAccount = null;
		
		if((clientAccount = login(clientUsername, clientPassword)) != null) {
			int clientId = clientAccount.getProfile().getBase().getId();
			ChannelMessage channelMessage = ClientMessages.newChannelMessage(messageId, messageText, clientId, clientUsername, channelId, messageTime, attachedFiles);		
			databaseManager.getChannelMessageDatabase().addItem(channelMessage);
			
			List<ChannelMessage> messages = new ArrayList<ChannelMessage>();
			messages.add(channelMessage);
			ChannelMessagePostEvent eventMessage = ClientEvents.newChannelMessagePostEvent(eventId, clientId, messages);
			for(SelectionKey key : selector.keys())
				if(key.channel() instanceof SocketChannel)
					sendMessage(key, eventMessage);
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(eventId, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleLoginRequest(SelectionKey clientKey, LoginRequest request) {
		String clientUsername = request.getRequestBase().getUsername();
		String clientPassword = request.getRequestBase().getPassword();
		ClientAccount clientAccount = login(clientUsername, clientPassword);
		
		if(clientAccount != null) {
			List<Channel> serverChannels = channelManager.getChannels();//databaseManager.getClientChannelDatabase().getLoadedItems();
			List<ClientProfile> serverMembers = new ArrayList<ClientProfile>();
			serverMembers.addAll(clientManager.getOnlineMembers());
			serverMembers.addAll(clientManager.getOfflineMembers());
			
			LoginEvent loginEvent = ClientEvents.newLoginEvent(1, clientAccount, serverChannels, serverMembers);
			sendMessage(clientKey, loginEvent);
			
			int clientId = clientAccount.getProfile().getBase().getId();
			EventBase eventBase = EventBase.newBuilder().setEventId(1).setEventTimestamp(System.currentTimeMillis()).build();
			ClientBase clientBase = ClientBase.newBuilder().setId(clientId).setUsername(clientUsername).build();
			for(SelectionKey key : selector.keys())
				if(key.channel() instanceof SocketChannel)
					sendMessage(key, ClientLoggedInEvent.newBuilder().setEventBase(eventBase).setClientBase(clientBase).build());
			//System.out.println("Server sent: " + loginEvent.toByteArray().length + " bytes");
		}
		else {
			//RequestType hinzufuegen, damit Rejections zugeordnet werden koennen
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleLogoutRequest(SelectionKey clientKey, LogoutRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleRegistrationRequest(SelectionKey clientKey, RegistrationRequest request) {
		String clientUsername = request.getUsername();
		String clientPassword = request.getPassword();
		String clientPasswordRepeat = request.getPasswordRepeat();
		
		if(clientPassword.equals(clientPasswordRepeat) && !databaseManager.getClientAccountDatabase().usernameExists(clientUsername)) {
			ClientProfile newProfile = ClientIdentities.newClientProfile(	databaseManager.getClientAccountDatabase().generateUniqueBaseId(), 
																			clientUsername, 
																			ClientStatus.ONLINE, 
																			ClientImages.getDefaultInstance(), 
																			ClientBadges.getDefaultInstance(), 
																			ClientFriends.getDefaultInstance(), 
																			ClientGroups.getDefaultInstance(), 
																			ClientIdentities.newClientDate(), 
																			ClientIdentities.newClientDate());
			ClientAccount newAccount = ClientIdentities.newClientAccount(newProfile, clientPassword);
			if(databaseManager.getClientAccountDatabase().addItem(newAccount)) {
				List<Channel> serverChannels = databaseManager.getClientChannelDatabase().getLoadedItems();
				List<ClientProfile> serverMembers = new ArrayList<ClientProfile>();
				for(ClientAccount currentAccount : databaseManager.getClientAccountDatabase().getLoadedItems())
					serverMembers.add(currentAccount.getProfile());
				LoginEvent loginEvent = ClientEvents.newLoginEvent(1, newAccount, serverChannels, serverMembers);
				sendMessage(clientKey, loginEvent);
				System.out.println("Server sent: " + loginEvent.toByteArray().length + " bytes");
			}
			else
				;//Send error message
		}
		else {
			//RequestType hinzufuegen, damit Rejections zugeordnet werden koennen
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelFileDownloadRequest(SelectionKey clientKey, ChannelFileDownloadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelFileUploadRequest(SelectionKey clientKey, ChannelFileUploadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateFileDownloadRequest(SelectionKey clientKey, PrivateFileDownloadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateFileUploadRequest(SelectionKey clientKey, PrivateFileUploadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePingMeasurementRequest(SelectionKey clientKey, PingMeasurementRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateMessagePostRequest(SelectionKey clientKey, PrivateMessagePostRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateMessageGetRequest(SelectionKey clientKey, PrivateMessageGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleProfileGetRequest(SelectionKey clientKey, ProfileGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			RequestRejectedEvent rejMessage = ClientEvents.newRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	public ObjectEventHandler<ProtobufMessage> getOnMessageReceived() {
		return onMessageReceivedHandler;
	}

	public void setOnMessageReceived(ObjectEventHandler<ProtobufMessage> handler) {
		onMessageReceivedHandler = handler;
	}
	
	public ObjectEventHandler<ProtobufMessage> getOnMessageSent() {
		return onMessageSentHandler;
	}
	
	public void setOnMessageSent(ObjectEventHandler<ProtobufMessage> handler) {
		onMessageSentHandler = handler;
	}
	
	public ObjectEventHandler<ProtobufMessage> getOnMessageTimedOut() {
		return onMessageTimedOutHandler;
	}
	
	public void setOnMessageTimedOut(ObjectEventHandler<ProtobufMessage> handler) {
		onMessageTimedOutHandler = handler;
	}
	
	public ObjectEventHandler<Throwable> getOnConnectionLost() {
		return onConnectionLostHandler;
	}
	
	public void setOnConnectionLost(ObjectEventHandler<Throwable> handler) {
		onConnectionLostHandler = handler;
	}
}
