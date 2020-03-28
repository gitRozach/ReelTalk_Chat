package network.peer.client;

import java.util.HashMap;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;

import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import network.messages.ProtobufMessage;
import network.peer.client.callbacks.PeerCallback;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.PrivateMessage;

public class ReelTalkClient extends SecuredProtobufClient {
	protected String clientUsername;
	protected String clientPassword;
	protected ClientProfile clientProfile;
	
	protected HashMap<Integer, List<ChannelMessage>> channelMessages;
	protected HashMap<Integer, List<PrivateMessage>> privateMessages;
	
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;
	
	public ReelTalkClient(String protocol, String remoteAddress, int port) throws Exception {
		super(protocol, remoteAddress, port);
		initProperties();
		initHandlers();
		initCallbacks();
	}
	
	private void initProperties() {
		clientUsername = "";
		clientPassword = "";
		clientProfile = null;
		channelMessages = new HashMap<Integer, List<ChannelMessage>>();
		privateMessages = new HashMap<Integer, List<PrivateMessage>>();
	}

	private void initHandlers() {
		onMessageReceivedHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {System.out.println("Client received a message.");}
		};
		
		onMessageSentHandler = new ObjectEventHandler<ProtobufMessage>() {
			@Override
			public void handle(ObjectEvent<ProtobufMessage> event) {System.out.println("Client sent a message.");}
		};
	}
	
	private void initCallbacks() {
		setPeerCallback(new PeerCallback() {
			@Override
			public void messageReceived(ProtobufMessage byteMessage) {
				onMessageReceivedHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, byteMessage) {
					private static final long serialVersionUID = 6882651385899629774L;
				});
			}
			@Override
			public void messageSent(ProtobufMessage byteMessage) {
				onMessageSentHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, byteMessage) {
					private static final long serialVersionUID = 2936930616791779331L;
				});
			}
			@Override
			public void connectionLost(Throwable throwable) {
				
			}
		});
	}
	
	public GeneratedMessageV3 readMessage() {
    	ProtobufMessage message = pollReceptionBytes();
    	if(message == null)
    		return null;
    	return message.getMessage();
    }
    
    public GeneratedMessageV3 readMessage(Class<?> messageClass) {
    	for(ProtobufMessage currentMessage : receivedMessages) {
    		if(currentMessage.hasMessage() && currentMessage.getMessage().getClass().equals(messageClass))
    			return currentMessage.getMessage();
    	}
    	return null;
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
	
	public String getClientUsername() {
		return clientUsername;
	}
	
	public void setClientUsername(String value) {
		clientUsername = value;
	}
	
	public String getClientPassword() {
		return clientPassword;
	}
	
	public void setClientPassword(String value) {
		clientPassword = value;
	}
	
	public ClientProfile getClientProfile() {
		return clientProfile;
	}
	
	public void setClientProfile(ClientProfile profile) {
		clientProfile = profile;
	}
	
	public List<ChannelMessage> getBufferedChannelMessages(int channelId) {
		return channelMessages.get(channelId);
	}
	
	public boolean hasBufferedChannelMessages(int channelId) {
		return channelMessages.get(channelId) != null;
	}
	
	public List<PrivateMessage> getBufferedPrivateMessages(int otherClientId) {
		return privateMessages.get(otherClientId);
	}
	
	public boolean hasBufferedPrivateMessages(int otherClientId) {
		return privateMessages.get(otherClientId) != null;
	}
}
