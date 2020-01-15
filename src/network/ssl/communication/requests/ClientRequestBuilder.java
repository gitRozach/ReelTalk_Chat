package network.ssl.communication.requests;

import network.ssl.communication.MessagePacket;

public class ClientRequestBuilder {
	private MessagePacket request;
	private String username;
	private String password;
	
	public ClientRequestBuilder() {
		this("", "");
	}
	
	public ClientRequestBuilder(String username, String password) {
		this.request = null;
		this.username = username;
		this.password = password;
	}
	
	public ClientRequestBuilder withLoginData(String username, String password) {
		this.username = username;
		this.password = password;
		return this;
	}
	
	public ClientRequestBuilder newChannelMessageRequest(int channelId, String message) {
		request = new ChannelMessageRequest(username, password, channelId, message);
		return this;
	}
	
	public ClientRequestBuilder newPrivateMessageRequest(int receiverId, String message) {
		request = new PrivateMessageRequest(username, password, receiverId, message);
		return this;
	}
	
	public MessagePacket build() {
		return request;
	}
}
