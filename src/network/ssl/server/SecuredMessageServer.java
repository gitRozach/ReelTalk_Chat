package network.ssl.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.google.protobuf.GeneratedMessageV3;

import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import network.ssl.client.callbacks.PeerCallback;
import network.ssl.communication.ProtobufMessage;
import network.ssl.server.manager.protobufDatabase.ClientAccountManager;
import network.ssl.server.manager.protobufDatabase.ClientChannelManager;
import network.ssl.server.manager.protobufDatabase.ClientMessageManager;
import protobuf.ClientEvents.ChannelMessageDataEvent;
import protobuf.ClientEvents.ChannelMessageEvent;
import protobuf.ClientEvents.ClientProfileDataEvent;
import protobuf.ClientEvents.ClientRequestDeniedEvent;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientMessages.ChannelClientMessage;
import protobuf.ClientRequests.ChannelJoinRequest;
import protobuf.ClientRequests.ChannelLeaveRequest;
import protobuf.ClientRequests.ChannelMessageDataRequest;
import protobuf.ClientRequests.ChannelMessageRequest;
import protobuf.ClientRequests.ClientFileDownloadRequest;
import protobuf.ClientRequests.ClientFileUploadRequest;
import protobuf.ClientRequests.ClientLoginRequest;
import protobuf.ClientRequests.ClientLogoutRequest;
import protobuf.ClientRequests.ClientPingMeasurementRequest;
import protobuf.ClientRequests.ClientProfileDataRequest;
import protobuf.ClientRequests.ClientRegistrationRequest;
import protobuf.ClientRequests.PrivateMessageRequest;

public class SecuredMessageServer extends SecuredServer {
	protected ClientAccountManager clients;
	protected ClientChannelManager channelManager;
	protected ClientMessageManager messageManager;
	
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;

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
		channelManager = new ClientChannelManager("src/clientData/channels.txt");
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
		onMessageReceivedHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {System.out.println("Server received a message.");}
		};
		
		onMessageSentHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {System.out.println("Server sent a message.");}
		};
	}
	
	private void initCallbacks() {
		setPeerCallback(new PeerCallback() {
			@Override
			public void messageReceived(ProtobufMessage message) {
				handleMessageReception(message);
				onMessageReceivedHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message) {
					private static final long serialVersionUID = -1115235010001672312L;
				});	
			}
			@Override
			public void messageSent(ProtobufMessage message) {
				onMessageSentHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message) {
					private static final long serialVersionUID = 8588402449968090480L;
				});
			}
			@Override
			public void connectionLost(Throwable throwable) {
				
			}
		});
	}
	
	public boolean checkLogin(String username, String password) {
		return login(username, password) != null;
	}
	
	public ClientAccount login(String username, String password) {
		return clients.getByUsernameAndPassword(username, password);
	}
	
	private void handleMessageReception(ProtobufMessage receivedMessage) {
		if(!receivedMessage.hasMessage())
			return;
		handleRequest(getLocalSelectionKey(receivedMessage.getSocketChannel()), receivedMessage.getMessage());
	}
	
	private void handleRequest(SelectionKey clientKey, GeneratedMessageV3 request) {
		if(request != null) {
			switch(request.getClass().getSimpleName()) {
			case "ChannelDataRequest":
				handleChannelDataRequest(clientKey, (ChannelMessageDataRequest)request);
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
				handleFileDownloadRequest(clientKey, (ClientFileDownloadRequest)request);
				break;
			case "FileUploadRequest":
				handleFileUploadRequest(clientKey, (ClientFileUploadRequest)request);
				break;
			case "PingRequest":
				handlePingRequest(clientKey, (ClientPingMeasurementRequest)request);
				break;
			case "PrivateMessageRequest":
				handlePrivateMessageRequest(clientKey, (PrivateMessageRequest)request);
				break;
			case "ProfileDataRequest":
				handleProfileDataRequest(clientKey, (ClientProfileDataRequest)request);
				break;
			}
		}
	}
	
	private void handleChannelDataRequest(SelectionKey clientKey, ChannelMessageDataRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			ChannelMessageDataEvent channelEvent = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), channelEvent));
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handleChannelJoinRequest(SelectionKey clientKey, ChannelJoinRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handleChannelLeaveRequest(SelectionKey clientKey, ChannelLeaveRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handleChannelMessageRequest(SelectionKey clientKey, ChannelMessageRequest request) {
		for(SelectionKey key : selector.keys()) {
			if(key.channel() instanceof SocketChannel) {
				ChannelMessageEvent eventMessage = null;
				sendMessage(new ProtobufMessage(key, eventMessage));
				messageManager.addChannelMessage(eventMessage.getChannelMessage());
			}
		}
	}
	
	private void handleClientLoginRequest(SelectionKey clientKey, ClientLoginRequest request) {
		ClientAccount clientData = login(request.getRequestedUsername(), request.getRequestedPassword());
		if(clientData != null) {
			ClientProfileDataEvent dataMessage = null;
			sendMessage(clientKey, new ProtobufMessage(clientKey, dataMessage));
			for(ChannelClientMessage message : messageManager.getChannelMessages()) {
				sendMessage(new ProtobufMessage(clientKey, message));
				try {
					Thread.sleep(50L);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage(clientKey, rejMessage));
		}
	}
	
	private void handleClientLogoutRequest(SelectionKey clientKey, ClientLogoutRequest request) {
		if(checkLogin(request.getRequestedUsername(), request.getRequestedPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handleClientRegistrationRequest(SelectionKey clientKey, ClientRegistrationRequest request) {
		
	}
	
	private void handleFileDownloadRequest(SelectionKey clientKey, ClientFileDownloadRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handleFileUploadRequest(SelectionKey clientKey, ClientFileUploadRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handlePingRequest(SelectionKey clientKey, ClientPingMeasurementRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handlePrivateMessageRequest(SelectionKey clientKey, PrivateMessageRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
		}
	}
	
	private void handleProfileDataRequest(SelectionKey clientKey, ClientProfileDataRequest request) {
		if(checkLogin(request.getRequestorBase().getRequestorUsername(), request.getRequestorBase().getRequestorPassword())) {
			
		}
		else {
			ClientRequestDeniedEvent rejMessage = null;
			sendMessage(clientKey, new ProtobufMessage((SocketChannel)clientKey.channel(), rejMessage));
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
