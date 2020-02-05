package network.ssl.client;

import network.client.eventHandlers.ObjectEvent;
import network.client.eventHandlers.ObjectEventHandler;
import network.ssl.communication.ByteMessage;
import network.ssl.communication.MessagePacket;

public class SecuredChatClient extends SecuredClient {
	protected ObjectEventHandler onMessageReceivedHandler;
	protected ObjectEventHandler onMessageSentHandler;
	
	public SecuredChatClient(String protocol, String remoteAddress, int port) throws Exception {
		super(protocol, remoteAddress, port);
		initHandlers();
	}

	private void initHandlers() {
		onMessageReceivedHandler = new ObjectEventHandler() {
			@Override
			public void handle(ObjectEvent event) {System.out.println("Client received a message.");}
		};
		
		onMessageSentHandler = new ObjectEventHandler() {
			@Override
			public void handle(ObjectEvent event) {System.out.println("Client sent a message.");}
		};
	}
	
	public void sendMessage(MessagePacket message) {
		sendBytes(message.serialize());
//		try {
//			Thread.sleep(50L);
//		} 
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
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

	@Override
	public void onBytesReceived(ByteMessage byteMessage) {
		MessagePacket receivedBytes = MessagePacket.deserialize(byteMessage.getMessageBytes());
		if(receivedBytes == null)
			return;
		onMessageReceivedHandler.handle(new ObjectEvent(ObjectEvent.ANY, receivedBytes) {
			private static final long serialVersionUID = 6882651385899629774L;
		});
	}
	
	@Override
	public void onBytesSent(ByteMessage byteMessage) {
		MessagePacket sentBytes = MessagePacket.deserialize(byteMessage.getMessageBytes());
		if(sentBytes == null)
			return;
		onMessageSentHandler.handle(new ObjectEvent(ObjectEvent.ANY, sentBytes) {
			private static final long serialVersionUID = 6882651385899629774L;
		});
	}
	
	public ObjectEventHandler getOnMessageReceived() {
		return onMessageReceivedHandler;
	}

	public void setOnMessageReceived(ObjectEventHandler handler) {
		onMessageReceivedHandler = handler;
	}
	
	public ObjectEventHandler getOnMessageSent() {
		return onMessageSentHandler;
	}
	
	public void setOnMessageSent(ObjectEventHandler handler) {
		onMessageSentHandler = handler;
	}
}
