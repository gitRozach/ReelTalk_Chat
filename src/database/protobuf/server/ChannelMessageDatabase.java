package database.protobuf.server;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;

public class ChannelMessageDatabase extends ProtobufFileDatabase<ChannelMessage>{
	protected final String CHANNEL_MESSAGE_FILE_NAME_PREFIX = "channelMessages";
	protected final String CHANNEL_MESSAGE_NAME_VALUE_SEPARATOR = "_";
	protected final String CHANNEL_MESSAGE_FILE_TYPE = ".txt";
	
	private final Object idLock = new Object(); 
	
	public ChannelMessageDatabase() throws IOException {
		this("", "");
	}
	
	public ChannelMessageDatabase(String databaseName) throws IOException {
		this(databaseName, "");
	}
	
	public ChannelMessageDatabase(String databaseName, String filePath) throws IOException {
		super(ChannelMessage.class, databaseName, filePath);
	}
	
	public String createChannelMessageFileNameFromId(int channelId) {
		return CHANNEL_MESSAGE_FILE_NAME_PREFIX + CHANNEL_MESSAGE_NAME_VALUE_SEPARATOR + channelId + CHANNEL_MESSAGE_FILE_TYPE;
	}
	
	@Override
	public void sort(List<ChannelMessage> items) {
		items.sort(ChannelMessageComparator);
	}
	
	public List<ChannelMessage> getChannelMessages(int maxAmount) {
		int startIndex = loadedItems.size() - maxAmount >= 0 ? loadedItems.size() - maxAmount : 0;
		return getChannelMessages(startIndex, loadedItems.size() - 1);
	}
	
	public List<ChannelMessage> getChannelMessages(int startIndex, int maxAmount) {
		if(startIndex < 0 || startIndex >= loadedItems.size())
			return null;
		int endIndex = (startIndex + maxAmount) >= loadedItems.size() ? loadedItems.size() - 1 : startIndex + maxAmount;
		return getItems(startIndex, endIndex);
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
	
	public static Comparator<ChannelMessage> ChannelMessageComparator = new Comparator<ChannelMessage>() {
		@Override
		public int compare(ChannelMessage o1, ChannelMessage o2) {
			return o1.getMessageBase().getMessageId() - o2.getMessageBase().getMessageId();
		}
	};
}
