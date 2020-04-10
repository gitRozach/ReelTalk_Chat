package network.peer.client;

import com.google.protobuf.Message;

import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import network.messages.ProtobufMessage;
import network.peer.callbacks.PeerCallback;
import network.peer.client.manager.ReelTalkIdentityManager;
import network.peer.manager.ReelTalkChannelManager;
import network.peer.manager.ReelTalkClientProfileManager;
import network.peer.manager.ReelTalkConfigurationManager;
import network.peer.manager.ReelTalkMessageManager;
import network.peer.manager.ReelTalkRequestManager;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientChannelJoinEvent;
import protobuf.ClientEvents.ClientLoginEvent;
import protobuf.ClientEvents.ClientProfileGetEvent;

public class ReelTalkClient extends SecuredProtobufClient {
	protected ReelTalkIdentityManager identityManager;
	protected ReelTalkConfigurationManager configManager;
	protected ReelTalkChannelManager channelManager;
	protected ReelTalkClientProfileManager profileManager;
	protected ReelTalkMessageManager messageManager;
	protected ReelTalkRequestManager requestManager;
	
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageTimedOutHandler;
	
	public ReelTalkClient(String protocol, String remoteAddress, int port) throws Exception {
		super(protocol, remoteAddress, port);
		initialize();
	}
	
	public Message readMessage() {
    	ProtobufMessage message = pollReceptionBytes();
    	if(message == null)
    		return null;
    	return message.getMessage();
    }
    
    public Message readMessage(Class<? extends Message> messageClass) {
    	for(ProtobufMessage currentMessage : receivedMessages) {
    		if(currentMessage.hasMessage() && currentMessage.getMessage().getClass().equals(messageClass))
    			return currentMessage.getMessage();
    	}
    	return null;
    }
    
    public void initialize() {
    	initManagers();
		initHandlers();
		initCallbacks();
    }
	
	private void initManagers() {
		identityManager = new ReelTalkIdentityManager();
		configManager = new ReelTalkConfigurationManager();
		channelManager = new ReelTalkChannelManager();
		profileManager = new ReelTalkClientProfileManager();
		messageManager = new ReelTalkMessageManager();
		requestManager = new ReelTalkRequestManager();
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
	
	public void handleReceivedMessage(ProtobufMessage message) {
		Message event = message.getMessage();
		if(event == null)
			return;
		if(event instanceof ClientProfileGetEvent) {
			
		}
		else if(event instanceof ChannelMessagePostEvent) {
		
		}
		else if(event instanceof ChannelMessageGetEvent) {
			
		}
		else if(event instanceof ClientChannelJoinEvent) {

		}
		else if(event instanceof ClientLoginEvent) {
			ClientLoginEvent loginEvent = (ClientLoginEvent) event;
			identityManager.setClientAccount(loginEvent.getAccount());
			channelManager.addChannels(loginEvent.getServerChannelList());
			profileManager.addClients(loginEvent.getMemberProfileList());
		}
	}
	
	public void handleSentMessage(ProtobufMessage message) {
		Message event = message.getMessage();
		if(event == null)
			return;
	}
	
	public void handleTimedOutMessage(ProtobufMessage message) {
		Message event = message.getMessage();
		if(event == null)
			return;
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
	
	public ReelTalkIdentityManager getIdentityManager() {
		return identityManager;
	}
	
	public ReelTalkRequestManager getRequestManager() {
		return requestManager;
	}
}
