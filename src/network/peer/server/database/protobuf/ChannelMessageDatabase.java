package network.peer.server.database.protobuf;

import java.io.IOException;
import java.util.Comparator;

import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;

public class ChannelMessageDatabase extends ProtobufFileDatabase<ChannelMessage>{
	private final Object idLock = new Object(); 
	
	public ChannelMessageDatabase() throws IOException {
		super(ChannelMessage.class);
	}
	
	public int generateUniqueMessageId() {
		return getLoadedItems().isEmpty() ? 1 : generateUniqueMessageId(getItem(getLoadedItems().size() - 1).getMessageBase().getMessageId() + 1); 
	}
	
	public int generateUniqueMessageId(int minId) {
		return generateUniqueMessageId(minId, Integer.MAX_VALUE);
	}
	
	public int generateUniqueMessageId(int minId, int maxId) {
		synchronized (idLock) {
			if(minId > maxId)
				return -1;
			if(getLoadedItems().isEmpty())
				return minId;
			boolean idAlreadyExists = false;
			for(int currentId = minId; currentId <= maxId; ++currentId) {
				idAlreadyExists = false;
				for(ChannelMessage currentMessage : getLoadedItems()) {
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
	
	public class ChannelMessageComparator implements Comparator<ChannelMessage> {
		@Override
		public int compare(ChannelMessage o1, ChannelMessage o2) {
			if(o1.getMessageBase().getMessageId() > o2.getMessageBase().getMessageId())
				return 1;
			else if(o1.getMessageBase().getMessageId() < o2.getMessageBase().getMessageId())
				return -1;
			else
				return 0;
		}
	}
}
