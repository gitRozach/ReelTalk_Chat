package network.ssl.client;

import network.ssl.communication.MessagePacket;

public class SecuredChatClient extends SecuredClient {
	public SecuredChatClient(String protocol, String remoteAddress, int port) throws Exception {
		super(protocol, remoteAddress, port);
	}
	
	public void sendMessage(MessagePacket message) {
		sendBytes(message.serialize());
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MessagePacket readMessage() {
    	byte[] messageBytes = readBytes();
    	if(messageBytes == null)
    		return null;
    	return MessagePacket.deserialize(messageBytes);
    }
    
    public MessagePacket readMessage(Class<?> messageClass) {
    	for(byte[] currentBytes : receptionQueue) {
    		MessagePacket currentPacket = MessagePacket.deserialize(currentBytes);
    		if(currentPacket != null && currentPacket.getClass().equals(messageClass))
    			return currentPacket;
    	}
    	return null;
    }

	@Override
	public void handleEvent(byte[] reception) {
		try {
			if(reception == null) {
				System.out.println("null reception");
				return;
			}
			System.out.println("Reception size: " + reception.length);
			MessagePacket event = MessagePacket.deserialize(reception);
			if(event == null) {
				System.out.println("Returning...");
				return;
			}
			switch(event.getClass().getSimpleName()) {
			case "RequestDeniedEvent":
				break;
			case "AccountDataEvent":
				break;
			case "ChannelDataEvent":
				break;
			case "ChannelMessageEvent":
				break;
			case "ClientJoinedChannelEvent":
				break;
			case "ClientLeftChannelEvent":
				break;
			case "ClientLoggedInEvent":
				System.out.println("LOGGED IN EVENT!");
				break;
			case "ClientLoggedOutEvent":
				break;
			case "ClientRegisteredEvent":
				break;
			case "FileDownloadEvent":
				break;
			case "FileUploadEvent":
				break;
			case "PingEvent":
				break;
			case "PrivateMessageEvent":
				System.out.println("Private Message!");
				break;
			case "ProfileDataEvent":
				break;
			}	
		}
		catch(Exception e) {
			log.info(e.toString());
		}	
	}
}
