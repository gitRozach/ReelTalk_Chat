package network.peer.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;

import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import network.messages.ProtobufMessage;
import network.peer.client.callbacks.PeerCallback;
import network.peer.server.database.manager.ClientMessageDatabase;
import network.peer.server.database.protobuf.ClientAccountDatabase;
import network.peer.server.database.protobuf.ClientChannelDatabase;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientRequestRejectedEvent;
import protobuf.ClientIdentities.ClientAccount;
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
	protected ClientAccountDatabase clients;
	protected ClientChannelDatabase channelManager;
	protected ClientMessageDatabase messageManager;
	
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;

	public ReelTalkServer(String protocol, String hostAddress, int port) throws Exception {
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
		clients = new ClientAccountDatabase();
		return clients.loadFileItems("src/clientData/accounts.txt", true);
	}
	
	private int initChannelManager() throws IOException {
		channelManager = new ClientChannelDatabase();
		return channelManager.loadFileItems("src/clientData/channels.txt", true);
	}
	
	private void initMessageManager() throws IOException {
		messageManager = new ClientMessageDatabase();
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
				onMessageReceivedHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message) {private static final long serialVersionUID = -1115235010001672312L;});	
			}
			@Override
			public void messageSent(ProtobufMessage message) {
				onMessageSentHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message) {private static final long serialVersionUID = 8588402449968090480L;});
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
		List<ClientAccount> accounts = clients.getByUsernameAndPassword(username, password);
		return accounts.isEmpty() ? null : accounts.get(0);
	}
	
	private void handleMessageReception(ProtobufMessage receivedMessage) {
		if(!receivedMessage.hasMessage())
			return;
		handleRequest(getLocalSelectionKey(receivedMessage.getSocketChannel()), receivedMessage.getMessage());
	}
	
	private void handleRequest(SelectionKey clientKey, GeneratedMessageV3 request) {
		if(request != null) {
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
	}
	
	private void handleChannelJoinRequest(SelectionKey clientKey, ChannelJoinRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelLeaveRequest(SelectionKey clientKey, ChannelLeaveRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelMessageGetRequest(SelectionKey clientKey, ChannelMessageGetRequest request) {
		
	}
	
	private void handleChannelMessagePostRequest(SelectionKey clientKey, ChannelMessagePostRequest request) {
		for(SelectionKey key : selector.keys()) {
			if(key.channel() instanceof SocketChannel) {
				ChannelMessage channelMessage = ClientMessages.newChannelMessage(1, request.getMessageText(), 0, request.getRequestBase().getUsername(), 1, System.currentTimeMillis(), Collections.emptyList());
				List<ChannelMessage> messages = new ArrayList<ChannelMessage>();
				messages.add(channelMessage);
				
				ChannelMessagePostEvent eventMessage = ClientEvents.newChannelMessagePostEvent(request.getRequestBase().getRequestId(), 0, messages);
				sendMessage(key, eventMessage);
				messageManager.addChannelMessage(eventMessage.getMessage(0));
			}
		}
	}
	
	private void handleClientLoginRequest(SelectionKey clientKey, ClientLoginRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {

		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
		
//		ClientAccount clientData = login(request.getRequestBase().getUsername(), request.getRequestBase().getPassword());
//		if(clientData != null) {
//			ClientProfileGetEvent dataMessage = null;
//			sendMessage(clientKey, dataMessage);
//			for(ChannelMessage message : messageManager.getChannelMessages()) {
//				sendMessage(clientKey, message);
//				try {
//					Thread.sleep(50L);
//				} 
//				catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		else {
//			ClientRequestRejectedEvent rejMessage = null;
//			sendMessage(clientKey, rejMessage);
//		}
	}
	
	private void handleClientLogoutRequest(SelectionKey clientKey, ClientLogoutRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleClientRegistrationRequest(SelectionKey clientKey, ClientRegistrationRequest request) {
		
	}
	
	private void handleChannelFileDownloadRequest(SelectionKey clientKey, ChannelFileDownloadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleChannelFileUploadRequest(SelectionKey clientKey, ChannelFileUploadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateFileDownloadRequest(SelectionKey clientKey, PrivateFileDownloadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateFileUploadRequest(SelectionKey clientKey, PrivateFileUploadRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePingMeasurementRequest(SelectionKey clientKey, PingMeasurementRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateMessagePostRequest(SelectionKey clientKey, PrivateMessagePostRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handlePrivateMessageGetRequest(SelectionKey clientKey, PrivateMessageGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
			sendMessage(clientKey, rejMessage);
		}
	}
	
	private void handleClientProfileGetRequest(SelectionKey clientKey, ClientProfileGetRequest request) {
		if(checkLogin(request.getRequestBase().getUsername(), request.getRequestBase().getPassword())) {
			
		}
		else {
			ClientRequestRejectedEvent rejMessage = null;
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
