package network.ssl.client;

import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import network.ssl.client.callbacks.PeerCallback;
import network.ssl.communication.ByteMessage;
import network.ssl.communication.MessagePacket;

public class SecuredMessageClient extends SecuredClient {
	protected ObjectEventHandler<ByteMessage> onMessageReceivedHandler;
	protected ObjectEventHandler<ByteMessage> onMessageSentHandler;
	
	public SecuredMessageClient(String protocol, String remoteAddress, int port) throws Exception {
		super(protocol, remoteAddress, port);
		initHandlers();
		initCallbacks();
	}

	private void initHandlers() {
		onMessageReceivedHandler = new ObjectEventHandler<ByteMessage>() {
			@Override
			public void handle(ObjectEvent<ByteMessage> event) {System.out.println("Client received a message.");}
		};
		
		onMessageSentHandler = new ObjectEventHandler<ByteMessage>() {
			@Override
			public void handle(ObjectEvent<ByteMessage> event) {System.out.println("Client sent a message.");}
		};
	}
	
	private void initCallbacks() {
		setPeerCallback(new PeerCallback() {
			@Override
			public void messageReceived(ByteMessage byteMessage) {
				MessagePacket receivedBytes = MessagePacket.deserialize(byteMessage.getMessageBytes());
				if(receivedBytes == null)
					return;
				onMessageReceivedHandler.handle(new ObjectEvent<ByteMessage>(ObjectEvent.ANY, byteMessage) {
					private static final long serialVersionUID = 6882651385899629774L;
				});
			}
			@Override
			public void messageSent(ByteMessage byteMessage) {
				MessagePacket sentBytes = MessagePacket.deserialize(byteMessage.getMessageBytes());
				if(sentBytes == null)
					return;
				onMessageSentHandler.handle(new ObjectEvent<ByteMessage>(ObjectEvent.ANY, byteMessage) {
					private static final long serialVersionUID = 2936930616791779331L;
				});
			}
			@Override
			public void connectionLost(Throwable throwable) {
				
			}
		});
	}
	
	public void sendMessage(MessagePacket message) {
		sendBytes(message.serialize());
	}
	
	public MessagePacket readMessage() {
    	ByteMessage message = pollReceptionBytes();
    	if(message == null)
    		return null;
    	return MessagePacket.deserialize(message.getMessageBytes());
    }
    
    public MessagePacket readMessage(Class<?> messageClass) {
    	for(ByteMessage currentMessage : receivedBytes) {
    		MessagePacket currentPacket = MessagePacket.deserialize(currentMessage.getMessageBytes());
    		if(currentPacket != null && currentPacket.getClass().equals(messageClass))
    			return currentPacket;
    	}
    	return null;
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
