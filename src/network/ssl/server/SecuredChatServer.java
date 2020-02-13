package network.ssl.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import network.ssl.client.id.ClientAccountData;
import network.ssl.client.id.ClientData;
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
import network.ssl.server.manager.ClientDataManager;
import network.ssl.server.stringDatabase.database.channelDatabase.ServerChannelManager;

public class SecuredChatServer extends SecuredServer {
	protected ClientDataManager clients;
	protected ServerChannelManager channelManager;
	
	protected ObjectEventHandler<ByteMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ByteMessage> onMessageSentHandler;

	public SecuredChatServer(String protocol, String hostAddress, int port) throws Exception {
		super(protocol, hostAddress, port);
		initClientDatabase();
		initChannelManager();
		initHandlers();
	}
	
	private void initClientDatabase() throws IOException {
		clients = new ClientDataManager(ClientData.class, "src/clientData/accounts.txt");
		clients.initialize();
	}
	
	private void initChannelManager() throws IOException {
		channelManager = new ServerChannelManager("src/clientData/channels.txt");
		channelManager.initialize();
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
	
	public boolean sendMessage(SelectionKey receiverKey, MessagePacket message) {
		return sendBytes((SocketChannel)receiverKey.channel(), message.serialize());
	}
	
	public boolean sendMessage(SocketChannel receiverChannel, MessagePacket message) {
		return sendBytes(receiverChannel, message.serialize());
	}
	
	@Override
	public void onBytesReceived(ByteMessage byteMessage) {
		handleMessageReception(byteMessage);
		onMessageReceivedHandler.handle(new ObjectEvent<ByteMessage>(ObjectEvent.ANY, byteMessage) {
			private static final long serialVersionUID = -1115235010001672312L;
		});	
	}
	
	@Override
	public void onBytesSent(ByteMessage byteMessage) {
		onMessageSentHandler.handle(new ObjectEvent<ByteMessage>(ObjectEvent.ANY, byteMessage) {
			private static final long serialVersionUID = 8588402449968090480L;
		});
	}
	
	public boolean checkLogin(String username, String password) {
		return login(username, password) != null;
	}
	
	public ClientAccountData login(String username, String password) {
		String clientDataString = clients.getByProperty("username", username);
		if(clientDataString == null)
			return null;
		ClientAccountData clientData = new ClientAccountData(clientDataString);
		if(clientData != null && clientData.getPassword().equals(password))
			return clientData;
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
				ChannelMessageEvent eventMessage = new ChannelMessageEvent(0, request.getUsername(), request.getMessage());
				sendMessage(key, eventMessage);
			}
		}
	}
	
	private void handleClientLoginRequest(SelectionKey clientKey, ClientLoginRequest request) {
		ClientAccountData clientData = login(request.getUsername(), request.getPassword());
		if(clientData != null) {
			AccountDataEvent dataMessage = new AccountDataEvent(clientData);
			sendMessage(clientKey, dataMessage);
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
