package network.peer.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
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
import protobuf.ClientChannels.ClientChannel;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientChannelJoinEvent;
import protobuf.ClientEvents.ClientChannelLeaveEvent;
import protobuf.ClientEvents.ClientLoginEvent;
import protobuf.ClientEvents.ClientRequestRejectedEvent;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientRequests.ChannelFileDownloadRequest;
import protobuf.ClientRequests.ChannelFileUploadRequest;
import protobuf.ClientRequests.ChannelJoinRequest;
import protobuf.ClientRequests.ChannelLeaveRequest;
import protobuf.ClientRequests.ChannelMessageGetRequest;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.ClientRequests.ClientLoginRequest;
import protobuf.ClientRequests.ClientLogoutRequest;
import protobuf.ClientRequests.ClientProfileGetRequest;
import protobuf.ClientRequests.ClientRegistrationRequest;
import protobuf.ClientRequests.PingMeasurementRequest;
import protobuf.ClientRequests.PrivateFileDownloadRequest;
import protobuf.ClientRequests.PrivateFileUploadRequest;
import protobuf.ClientRequests.PrivateMessageGetRequest;
import protobuf.ClientRequests.PrivateMessagePostRequest;
import protobuf.wrapper.ClientEvents;
import protobuf.wrapper.ClientMessages;

public class ReelTalkServer extends SecuredProtobufServer {	
	protected ReelTalkConfigurationManager configManager;
	protected ReelTalkServerDatabaseManager databaseManager;
	protected ReelTalkClientProfileManager clientManager;
	protected ReelTalkChannelManager channelManager;
	
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageTimedOutHandler;

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
	
	public boolean checkLogin(String username, String password) {
		return login(username, password) != null;
	}
	
	public ClientAccount login(String username, String password) {
		List<ClientAccount> accounts = databaseManager.getClientAccountDatabase().getByUsernameAndPassword(username, password);
		return accounts.isEmpty() ? null : accounts.get(0);
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
			public void handle(ObjectEvent<ProtobufMessage> event) {System.out.println("Server received a message.");}
		};
		onMessageSentHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {System.out.println("Server sent a message.");}
		};
		onMessageTimedOutHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {System.out.println("Server message timed out.");}
		};
	}
	
	private void initCallbacks() {
		setPeerCallback(new PeerCallback() {
			@Override
			public void messageReceived(ProtobufMessage message) {
				handleReceivedMessage(message);
				onMessageReceivedHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message));	
			}
			@Override
			public void messageSent(ProtobufMessage message) {
				onMessageSentHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message));
			}
			@Override
			public void messageTimedOut(ProtobufMessage message) {
				onMessageTimedOutHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message));	
			}
			@Override
			public void connectionLost(Throwable throwable) {
				
			}
		});
	}
	
	private void handleReceivedMessage(ProtobufMessage receivedMessage) {
		if(!receivedMessage.hasSocketChannel() || !receivedMessage.hasMessage())
			return;
		handleRequest(getLocalSelectionKey(receivedMessage.getSocketChannel()), receivedMessage.getMessage());
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
		case "ClientLoginRequest":
			handleClientLoginRequest(clientKey, (ClientLoginRequest)request);
			break;
		case "ClientLogoutRequest":
			handleClientLogoutRequest(clientKey, (ClientLogoutRequest)request);
			break;
		case "ClientRegistrationRequest":
			handleClientRegistrationRequest(clientKey, (ClientRegistrationRequest)request);
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
		case "ClientPingMeasurementRequest":
			handlePingMeasurementRequest(clientKey, (PingMeasurementRequest)request);
			break;
		case "ClientProfileRequest":
			handleClientProfileGetRequest(clientKey, (ClientProfileGetRequest)request);
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
		String clientUsername = request.getRequestBase().getUsername();
		String clientPassword = request.getRequestBase().getPassword();
		if(checkLogin(clientUsername, clientPassword)) {
			ChannelMessageDatabase messageDb = databaseManager.getChannelMessageDatabase();
			messageDb.loadFileItems("src/clientData/channelMessages/" + messageDb.createChannelMessageFileNameFromId(request.getChannelBase().getChannelId()));
			List<ChannelMessage> channelMessages = messageDb.getChannelMessages(20);
			if(channelMessages == null)
				channelMessages = Collections.emptyList();
			ClientChannelJoinEvent joinEvent = ClientEvents.newClientChannelJoinEvent(1, request.getChannelBase().getChannelId(), channelMessages);
			sendMessage(clientKey, joinEvent);
			System.out.println("Server sent: " + joinEvent.toByteArray().length + " bytes");
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelLeaveRequest(SelectionKey clientKey, ChannelLeaveRequest request) {
		String clientUsername = request.getRequestBase().getUsername();
		String clientPassword = request.getRequestBase().getPassword();
		if(checkLogin(clientUsername, clientPassword)) {
			ClientAccount clientAccount = login(clientUsername, clientPassword);
			ClientChannelLeaveEvent leaveEvent = ClientEvents.newClientChannelLeaveEvent(1, request.getChannelBase().getChannelId(), clientAccount.getProfile());
			sendMessage(clientKey, leaveEvent);
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelMessageGetRequest(SelectionKey clientKey, ChannelMessageGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			int startIndex = request.getLastIndex() - (request.getMessageCount() - 1) < 0 ? 0 : request.getLastIndex() - (request.getMessageCount() - 1); 
			if(startIndex >= request.getLastIndex())
				return;
			ChannelMessageGetEvent channelMessage = ClientEvents.newChannelMessageGetEvent(	1, 
																							1, 
																							databaseManager.getChannelMessageDatabase().getItems(startIndex , request.getLastIndex()));
			sendMessage(clientKey, channelMessage);
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelMessagePostRequest(SelectionKey clientKey, ChannelMessagePostRequest request) {
		String clientUsername = request.getRequestBase().getUsername();
		String clientPassword = request.getRequestBase().getPassword();
		int channelId = request.getChannelBase().getChannelId();
		if(checkLogin(clientUsername, clientPassword)) {
			ClientAccount clientAccount = login(clientUsername, clientPassword);
			int clientId = clientAccount.getProfile().getBase().getId();
			
			for(SelectionKey key : selector.keys()) {
				if(key.channel() instanceof SocketChannel) {
					ChannelMessage channelMessage = ClientMessages.newChannelMessage(1, request.getMessageText(), clientId, clientUsername, channelId, System.currentTimeMillis(), Collections.emptyList());
					List<ChannelMessage> messages = new ArrayList<ChannelMessage>();
					messages.add(channelMessage);
					
					ChannelMessagePostEvent eventMessage = ClientEvents.newChannelMessagePostEvent(request.getRequestBase().getRequestId(), 0, messages);
					sendMessage(key, eventMessage);
					databaseManager.getChannelMessageDatabase().addItem(eventMessage.getMessage(0));
				}
			}
		}
	}
	
	private void handleClientLoginRequest(SelectionKey clientKey, ClientLoginRequest request) {
		String username = request.getRequestBase().getUsername();
		String password = request.getRequestBase().getPassword();
		if(checkLogin(username, password)) {
			System.out.println("Logged in");
			ClientAccount clientData = login(username, password);
			if(clientData != null) {
				List<ClientChannel> serverChannels = databaseManager.getClientChannelDatabase().getLoadedItems();
				List<ClientProfile> serverMembers = new ArrayList<ClientProfile>();
				serverMembers.addAll(clientManager.getOnlineMembers());
				serverMembers.addAll(clientManager.getOfflineMembers());
				ClientLoginEvent loginEvent = ClientEvents.newClientLoginEvent(1, clientData, serverChannels, serverMembers);
				sendMessage(clientKey, loginEvent);
				System.out.println("Server sent: " + loginEvent.toByteArray().length + " bytes");
			}
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleClientLogoutRequest(SelectionKey clientKey, ClientLogoutRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleClientRegistrationRequest(SelectionKey clientKey, ClientRegistrationRequest request) {
		
	}
	
	private void handleChannelFileDownloadRequest(SelectionKey clientKey, ChannelFileDownloadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelFileUploadRequest(SelectionKey clientKey, ChannelFileUploadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateFileDownloadRequest(SelectionKey clientKey, PrivateFileDownloadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateFileUploadRequest(SelectionKey clientKey, PrivateFileUploadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePingMeasurementRequest(SelectionKey clientKey, PingMeasurementRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateMessagePostRequest(SelectionKey clientKey, PrivateMessagePostRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateMessageGetRequest(SelectionKey clientKey, PrivateMessageGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleClientProfileGetRequest(SelectionKey clientKey, ClientProfileGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = ClientEvents.newClientRequestRejectedEvent(1, "Invalid login data.");
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
}
