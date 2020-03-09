package network.ssl.client;

import com.google.protobuf.GeneratedMessageV3;

import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import network.ssl.client.callbacks.PeerCallback;
import network.ssl.communication.ProtobufMessage;

public class ReelTalkClient extends SecuredProtobufClient {
	protected ObjectEventHandler<ProtobufMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ProtobufMessage> onMessageSentHandler;
	
	public ReelTalkClient(String protocol, String remoteAddress, int port) throws Exception {
		super(protocol, remoteAddress, port);
		initHandlers();
		initCallbacks();
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
}
