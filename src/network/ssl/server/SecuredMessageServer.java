package network.ssl.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import network.ssl.client.callbacks.PeerCallback;
import network.ssl.communication.ByteMessage;
import network.ssl.communication.MessagePacket;
import network.ssl.communication.events.AccountDataEvent;
import network.ssl.communication.events.ChannelDataEvent;
import network.ssl.communication.events.ChannelMessageEvent;
import network.ssl.communication.events.RequestDeniedEvent;
import network.ssl.communication.requests.ChannelDataRequest;
import network.ssl.communication.requests.ChannelJoinRequest;
import network.ssl.communication.requests.ChannelLeaveRequest;
import network.ssl.communication.requests.ChannelMessageRequest;
import network.ssl.communication.requests.ClassifiedRequest;
import network.ssl.communication.requests.ClientLoginRequest;
import network.ssl.communication.requests.ClientLogoutRequest;
import network.ssl.communication.requests.ClientRegistrationRequest;
import network.ssl.communication.requests.FileDownloadRequest;
import network.ssl.communication.requests.FileUploadRequest;
import network.ssl.communication.requests.PingRequest;
import network.ssl.communication.requests.PrivateMessageRequest;
import network.ssl.communication.requests.ProfileDataRequest;
import network.ssl.server.manager.channelDatabase.ServerChannelManager;
import network.ssl.server.manager.clientDataManager.ClientAccountManager;
import network.ssl.server.manager.clientDataManager.items.ClientAccountData;
import network.ssl.server.manager.messageManager.ClientMessageManager;
import network.ssl.server.manager.messageManager.items.ChannelMessage;

public class SecuredMessageServer extends SecuredServer {
	protected ClientAccountManager clients;
	protected ServerChannelManager channelManager;
	protected ClientMessageManager messageManager;
	
	protected ObjectEventHandler<ByteMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ByteMessage> onMessageSentHandler;

	public SecuredMessageServer(String protocol, String hostAddress, int port) throws Exception {
		super(protocol, hostAddress, port);
		int camRes = initClientDatabase();
		int cmRes = initChannelManager();
		initMessageManager();
		initHandlers();
		initCallbacks();
		logger.info(camRes + " Clients loaded.");
		logger.info(cmRes + " Channels loaded.");
	}
	
	private int initClientDatabase() throws IOException {
		clients = new ClientAccountManager("src/clientData/accounts.txt");
		return clients.initialize();
	}
	
	private int initChannelManager() throws IOException {
		channelManager = new ServerChannelManager("src/clientData/channels.txt");
		return channelManager.initialize();
	}
	
	private void initMessageManager() throws IOException {
		messageManager = new ClientMessageManager();
		messageManager.configurePrivateMessageManagerPath("src/clientData/messages/privateMessages.txt");
		messageManager.configureChannelMessageManagerPath("src/clientData/messages/channelMessages.txt");
		messageManager.configureProfileCommentManagerPath("src/clientData/messages/profileComments.txt");
		messageManager.initialize();
	}
	
	private void initHandlers() {
		onMessageReceivedHandler = new ObjectEventHandler<ByteMessage>() {
			@Override
			public void handle(ObjectEvent<ByteMessage> event) {System.out.println("Server received a message.");}
		};
		
		onMessageSentHandler = new ObjectEventHandler<ByteMessage>() {
			@Override
			public void handle(ObjectEvent<ByteMessage> event) {System.out.println("Server sent a message.");}
		};
	}
	
	private void initCallbacks() {
		setPeerCallback(new PeerCallback() {
			@Override
			public void messageReceived(ByteMessage byteMessage) {
				handleMessageReception(byteMessage);
				onMessageReceivedHandler.handle(new ObjectEvent<ByteMessage>(ObjectEvent.ANY, byteMessage) {
					private static final long serialVersionUID = -1115235010001672312L;
				});	
			}
			@Override
			public void messageSent(ByteMessage byteMessage) {
				onMessageSentHandler.handle(new ObjectEvent<ByteMessage>(ObjectEvent.ANY, byteMessage) {
					private static final long serialVersionUID = 8588402449968090480L;
				});
			}
			@Override
			public void connectionLost(Throwable throwable) {
				
			}
		});
	}
	
	public boolean sendMessage(SelectionKey receiverKey, MessagePacket message) {
		return sendBytes((SocketChannel)receiverKey.channel(), message.serialize());
	}
	
	public boolean sendMessage(SocketChannel receiverChannel, MessagePacket message) {
		return sendBytes(receiverChannel, message.serialize());
	}
	
	public boolean checkLogin(String username, String password) {
		return login(username, password) != null;
	}
	
	public ClientAccountData login(String username, String password) {
		String[] clientDataStrings = clients.getByProperty("username", username);
		if(clientDataStrings == null)
			return null;
		for(String currentClientDataString : clientDataStrings) {
			ClientAccountData clientData = new ClientAccountData(currentClientDataString);
			if(clientData != null && clientData.getPassword().equals(password))
				return clientData;
		}
		return null;
	}
	
	private void handleMessageReception(ByteMessage receivedMessage) {
		MessagePacket messagePacket = MessagePacket.deserialize(receivedMessage.getMessageBytes());
		if(messagePacket instanceof ClassifiedRequest)
			handleRequest(receivedMessage.getSocketChannel().keyFor(selector), (ClassifiedRequest)messagePacket);
	}
	
	private void handleRequest(SelectionKey clientKey, ClassifiedRequest request) {
		if(request != null) {
			switch(request.getClass().getSimpleName()) {
			case "ChannelDataRequest":
				handleChannelDataRequest(clientKey, (ChannelDataRequest)request);
				break;
			case "ChannelJoinRequest":
				handleChannelJoinRequest(clientKey, (ChannelJoinRequest)request);
				break;
			case "ChannelLeaveRequest":
				handleChannelLeaveRequest(clientKey, (ChannelLeaveRequest)request);
				break;
			case "ChannelMessageRequest":
				handleChannelMessageRequest(clientKey, (ChannelMessageRequest)request);
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
			case "FileDownloadRequest":
				handleFileDownloadRequest(clientKey, (FileDownloadRequest)request);
				break;
			case "FileUploadRequest":
				handleFileUploadRequest(clientKey, (FileUploadRequest)request);
				break;
			case "PingRequest":
				handlePingRequest(clientKey, (PingRequest)request);
				break;
			case "PrivateMessageRequest":
				handlePrivateMessageRequest(clientKey, (PrivateMessageRequest)request);
				break;
			case "ProfileDataRequest":
				handleProfileDataRequest(clientKey, (ProfileDataRequest)request);
				break;
			}
		}
	}
	
	private void handleChannelDataRequest(SelectionKey clientKey, ChannelDataRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			ChannelDataEvent channelEvent = new ChannelDataEvent();
			//Fill channelEvent with data...
			sendMessage(clientKey, channelEvent);
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelJoinRequest(SelectionKey clientKey, ChannelJoinRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelLeaveRequest(SelectionKey clientKey, ChannelLeaveRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelMessageRequest(SelectionKey clientKey, ChannelMessageRequest request) {
		for(SelectionKey key : selector.keys()) {
			if(key.channel() instanceof SocketChannel) {
				ChannelMessageEvent eventMessage = new ChannelMessageEvent(0, 0, request.getUsername(), request.getMessage(), 0, "");
				sendMessage(key, eventMessage);
				messageManager.addChannelMessage(new ChannelMessage(eventMessage.getMessageId(), eventMessage.getSenderName(), 
																	eventMessage.getClientId(), eventMessage.getMessageText(), 
																	eventMessage.getChannelId(), eventMessage.getChannelName()));
			}
		}
	}
	
	private void handleClientLoginRequest(SelectionKey clientKey, ClientLoginRequest request) {
		ClientAccountData clientData = login(request.getUsername(), request.getPassword());
		if(clientData != null) {
			AccountDataEvent dataMessage = new AccountDataEvent(clientData);
			sendMessage(clientKey, dataMessage);
			for(ChannelMessage message : messageManager.getChannelMessages()) {
				sendMessage(clientKey, new ChannelMessageEvent(	message.getSenderId(), message.getMessageId(), 
																message.getSenderName(), message.getMessageText(),
																message.getChannelId(), message.getChannelName()));
				try {
					Thread.sleep(50L);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleClientLogoutRequest(SelectionKey clientKey, ClientLogoutRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleClientRegistrationRequest(SelectionKey clientKey, ClientRegistrationRequest request) {
		
	}
	
	private void handleFileDownloadRequest(SelectionKey clientKey, FileDownloadRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleFileUploadRequest(SelectionKey clientKey, FileUploadRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePingRequest(SelectionKey clientKey, PingRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateMessageRequest(SelectionKey clientKey, PrivateMessageRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleProfileDataRequest(SelectionKey clientKey, ProfileDataRequest request) {
		if(checkLogin(request.getUsername(), request.getPassword())) {
			
		}
		else {
			RequestDeniedEvent rejMessage = new RequestDeniedEvent(ClientLoginRequest.class);
			sendMessage(clientKey, rejMessage);
		}
	}
	
	public ObjectEventHandler<ByteMessage> getOnMessageReceived() {
		return onMessageReceivedHandler;
	}

	public void setOnMessageReceived(ObjectEventHandler<ByteMessage> handler) {
		onMessageReceivedHandler = handler;
	}
	
	public ObjectEventHandler<ByteMessage> getOnMessageSent() {
		return onMessageSentHandler;
	}
	
	public void setOnMessageSent(ObjectEventHandler<ByteMessage> handler) {
		onMessageSentHandler = handler;
	}
}
