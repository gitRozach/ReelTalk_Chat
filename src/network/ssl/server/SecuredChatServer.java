package network.ssl.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import network.ssl.client.id.ClientAccountData;
import network.ssl.communication.MessagePacket;
import network.ssl.communication.events.AccountDataEvent;
import network.ssl.communication.events.ChannelDataEvent;
import network.ssl.communication.events.ChannelMessageEvent;
import network.ssl.communication.events.RequestDeniedEvent;
import network.ssl.communication.requests.ChannelDataRequest;
import network.ssl.communication.requests.ChannelJoinRequest;
import network.ssl.communication.requests.ChannelLeaveRequest;
import network.ssl.communication.requests.ChannelMessageRequest;
import network.ssl.communication.requests.ClientLoginRequest;
import network.ssl.communication.requests.ClientLogoutRequest;
import network.ssl.communication.requests.ClientRegistrationRequest;
import network.ssl.communication.requests.FileDownloadRequest;
import network.ssl.communication.requests.FileUploadRequest;
import network.ssl.communication.requests.PingRequest;
import network.ssl.communication.requests.PrivateMessageRequest;
import network.ssl.communication.requests.ProfileDataRequest;
import network.ssl.server.database.ClientDatabase;

public class SecuredChatServer extends SecuredServer {
	private ClientDatabase<ClientAccountData> clients;

	public SecuredChatServer(String protocol, String hostAddress, int port) throws Exception {
		super(protocol, hostAddress, port);
		clients = new ClientDatabase<ClientAccountData>("src/clientData/accounts.txt");
	}
	
	public void sendMessage(SelectionKey receiverKey, MessagePacket message) {
		sendBytes(receiverKey, message.serialize());
	}
	
	public void sendMessage(SocketChannel receiverChannel, MessagePacket message) {
		sendBytes(receiverChannel, message.serialize());
	}
	
	@Override
	public void handleRequest(SelectionKey clientKey, byte[] requestBytes) {
		try {
			MessagePacket messagePacket = MessagePacket.deserialize(requestBytes);
			if(messagePacket != null) {
				switch(messagePacket.getClass().getSimpleName()) {
				case "ChannelDataRequest":
					handleChannelDataRequest(clientKey, (ChannelDataRequest)messagePacket);
					break;
				case "ChannelJoinRequest":
					handleChannelJoinRequest(clientKey, (ChannelJoinRequest)messagePacket);
					break;
				case "ChannelLeaveRequest":
					handleChannelLeaveRequest(clientKey, (ChannelLeaveRequest)messagePacket);
					break;
				case "ChannelMessageRequest":
					handleChannelMessageRequest(clientKey, (ChannelMessageRequest)messagePacket);
					break;
				case "ClientLoginRequest":
					handleClientLoginRequest(clientKey, (ClientLoginRequest)messagePacket);
					break;
				case "ClientLogoutRequest":
					handleClientLogoutRequest(clientKey, (ClientLogoutRequest)messagePacket);
					break;
				case "ClientRegistrationRequest":
					handleClientRegistrationRequest(clientKey, (ClientRegistrationRequest)messagePacket);
					break;
				case "FileDownloadRequest":
					handleFileDownloadRequest(clientKey, (FileDownloadRequest)messagePacket);
					break;
				case "FileUploadRequest":
					handleFileUploadRequest(clientKey, (FileUploadRequest)messagePacket);
					break;
				case "PingRequest":
					handlePingRequest(clientKey, (PingRequest)messagePacket);
					break;
				case "PrivateMessageRequest":
					handlePrivateMessageRequest(clientKey, (PrivateMessageRequest)messagePacket);
					break;
				case "ProfileDataRequest":
					handleProfileDataRequest(clientKey, (ProfileDataRequest)messagePacket);
					break;
				}
			}
		}
		catch(Exception e) {
			log.info(e.toString());
		}
	}
	
	@Override
	public void start() {
		super.start();
		try {
			clients.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
