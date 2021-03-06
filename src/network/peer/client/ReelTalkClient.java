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
import protobuf.ClientEvents.ChannelJoinEvent;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientJoinedChannelEvent;
import protobuf.ClientEvents.ClientLeftChannelEvent;
import protobuf.ClientEvents.ClientLoggedInEvent;
import protobuf.ClientEvents.ClientLoggedOutEvent;
import protobuf.ClientEvents.LoginEvent;
import protobuf.ClientEvents.LogoutEvent;
import protobuf.ClientEvents.ProfileGetEvent;

public class ReelTalkClient extends SecuredProtobufClient {
	protected ReelTalkIdentityManager identityManager;
	protected ReelTalkClientProfileManager profileManager;
	protected ReelTalkConfigurationManager configManager;
	protected ReelTalkChannelManager channelManager;
	protected ReelTalkMessageManager messageManager;
	protected ReelTalkRequestManager requestManager;
	
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageTimedOutHandler;
	protected ObjectEventHandler<Throwable> onConnectionLostHandler;
	
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
				onMessageReceivedHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message));
			}
			@Override
			public void messageSent(ProtobufMessage message) {
				handleSentMessage(message);
				onMessageSentHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message));
			}
			@Override
			public void messageTimedOut(ProtobufMessage message) {
				handleTimedOutMessage(message);
				onMessageTimedOutHandler.handle(new ObjectEvent<ProtobufMessage>(ObjectEvent.ANY, message));
			}
			@Override
			public void connectionLost(Throwable throwable) {
				handleLostConnection(throwable);
				onConnectionLostHandler.handle(new ObjectEvent<Throwable>(ObjectEvent.ANY, throwable));
			}
		});
	}
	
	public void handleReceivedMessage(ProtobufMessage message) {
		Message event = message.getMessage();
		if(event == null)
			return;
		else if(event instanceof ProfileGetEvent) {
			
		}
		else if(event instanceof ClientJoinedChannelEvent) {
			
		}
		else if(event instanceof ClientLeftChannelEvent) {
			
		}
		else if(event instanceof ChannelMessagePostEvent) {
		
		}
		else if(event instanceof ChannelMessageGetEvent) {
			
		}
		else if(event instanceof ChannelJoinEvent) {

		}
		else if(event instanceof ClientLoggedInEvent) {
			
		}
		else if(event instanceof ClientLoggedOutEvent) {
			
		}
		else if(event instanceof LoginEvent) {
			LoginEvent loginEvent = (LoginEvent) event;
			identityManager.setClientAccount(loginEvent.getAccount());
			channelManager.addChannels(loginEvent.getServerChannelList());
			profileManager.addClients(loginEvent.getMemberProfileList());
		}
		else if(event instanceof LogoutEvent) {
			
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
	
	public void handleLostConnection(Throwable throwable) {
		
	}
	
	public ReelTalkConfigurationManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ReelTalkConfigurationManager value) {
		configManager = value;
	}

	public ReelTalkChannelManager getChannelManager() {
		return channelManager;
	}

	public void setChannelManager(ReelTalkChannelManager value) {
		channelManager = value;
	}

	public ReelTalkClientProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(ReelTalkClientProfileManager value) {
		profileManager = value;
	}

	public ReelTalkMessageManager getMessageManager() {
		return messageManager;
	}

	public void setMessageManager(ReelTalkMessageManager value) {
		messageManager = value;
	}

	public void setIdentityManager(ReelTalkIdentityManager value) {
		identityManager = value;
	}

	public void setRequestManager(ReelTalkRequestManager value) {
		requestManager = value;
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
	
	public ReelTalkIdentityManager getIdentityManager() {
		return identityManager;
	}
	
	public ReelTalkRequestManager getRequestManager() {
		return requestManager;
	}
}
