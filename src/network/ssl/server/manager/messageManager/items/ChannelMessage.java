package network.ssl.server.manager.messageManager.items;

import java.util.Map;

import network.ssl.server.manager.messageManager.Message;

public class ChannelMessage extends Message {
	protected static final String CHANNEL_ID_PROPERTY_NAME = "channelId";
	protected static final String CHANNEL_NAME_PROPERTY_NAME = "channelName";
	
	protected static final int DEFAULT_CHANNEL_ID = -1;
	protected static final String DEFAULT_CHANNEL_NAME = "UNKNOWN_CHANNEL_NAME";
	
	protected int channelId;
	protected String channelName;
	
	public ChannelMessage() {
		this(DEFAULT_MESSAGE_ID, DEFAULT_SENDER_NAME, DEFAULT_SENDER_ID, DEFAULT_MESSAGE_TEXT);
	}
	
	public ChannelMessage(int messageId, String messageSenderName, int messageSenderId, String messageText) {
		this(messageId, messageSenderName, messageSenderId, messageText, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME);
	}
	
	public ChannelMessage(	int messageId, String messageSenderName, int messageSenderId, String messageText, 
							int messageChannelId, String messageChannelName) {
		super(messageId, messageSenderName, messageSenderId, messageText);
		channelId = messageChannelId;
		channelName = messageChannelName;
	}

	@Override
	public String toDatabaseString() {
		return 	super.toDatabaseString() + 
				CHANNEL_ID_PROPERTY_NAME + PROPERTY_START + getChannelId() + PROPERTY_END +
				CHANNEL_NAME_PROPERTY_NAME + PROPERTY_START + getChannelName() + PROPERTY_END;
	}

	@Override
	public int initFromDatabaseString(String databaseString) {
		int superRes = super.initFromDatabaseString(databaseString);
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(CHANNEL_ID_PROPERTY_NAME)) {
			setChannelId(Integer.parseInt(propertyValueMap.get(CHANNEL_ID_PROPERTY_NAME)));
			++counter;
		}
		if(propertyValueMap.containsKey(CHANNEL_NAME_PROPERTY_NAME)) {
			setChannelName(propertyValueMap.get(CHANNEL_NAME_PROPERTY_NAME));
			++counter;
		}
		return superRes + counter;
	}
	
	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int messageChannelId) {
		channelId = messageChannelId;
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String value) {
		channelName = value;
	}
}
