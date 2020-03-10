package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;

public class ChannelMessageManager extends ProtobufFileDatabase<ChannelMessage>{
	private final Object idLock = new Object(); 
	
	public ChannelMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public ChannelMessageManager(File databaseFile) throws IOException {
		super(ChannelMessage.class, databaseFile);
	}
	
	public int generateUniqueMessageId() {
		return getItems().isEmpty() ? 1 : generateUniqueMessageId(getItem(getItems().size() - 1).getMessageBase().getMessageId() + 1); 
	}
	
	public int generateUniqueMessageId(int minId) {
		return generateUniqueMessageId(minId, Integer.MAX_VALUE);
	}
	
	public int generateUniqueMessageId(int minId, int maxId) {
		synchronized (idLock) {
			if(minId > maxId)
				return -1;
			if(getItems().isEmpty())
				return minId;
			boolean idAlreadyExists = false;
			for(int currentId = minId; currentId <= maxId; ++currentId) {
				idAlreadyExists = false;
				for(ChannelMessage currentMessage : getItems()) {
					if(currentMessage.getMessageBase().getMessageId() == currentId) {
						idAlreadyExists = true;
						break;
					}
				}
				if(!idAlreadyExists)
					return currentId;
			}
			return -1;
		}
	}
	
	public static int generateAnswerIdFor(ChannelMessage message) {
		return message == null ? -1 : (message.getMessageAnswerCount() == 0 ? 1 :
		generateAnswerIdFor(message, message.getMessageAnswer(message.getMessageAnswerCount() - 1).getMessageBase().getMessageId() + 1));
	}
	
	public static int generateAnswerIdFor(ChannelMessage message, int minId) {
		return generateAnswerIdFor(message, minId, Integer.MAX_VALUE);
	}
	
	public static int generateAnswerIdFor(ChannelMessage message, int minId, int maxId) {
		if(message == null || minId > maxId)
			return -1;
		if(message.getMessageAnswerCount() == 0)
			return minId;
		boolean idAlreadyExists = false;
		for(int currentId = minId; currentId <= maxId; ++currentId) {
			idAlreadyExists = false;
			for(ChannelMessageAnswer currentAnswer : message.getMessageAnswerList()) {
				if(currentAnswer.getMessageBase().getMessageId() == currentId) {
					idAlreadyExists = true;
					break;
				}
			}
			if(!idAlreadyExists)
				return currentId;
		}
		return -1;
	}
	
}
