package network.ssl.server.stringDatabase.database.channelDatabase;

import java.util.Map;

import network.ssl.server.stringDatabase.database.propertyValueDatabase.messages.Message;

public class ServerChannel extends Message {	
	public static final String CHANNEL_ID_PROPERTY_NAME = "channelId";
	public static final String CHANNEL_NAME_PROPERTY_NAME = "channelName";
	
	public static final int DEFAULT_CHANNEL_ID = -1;
	public static final String DEFAULT_CHANNEL_NAME = "UNKNOWN_CHANNEL_NAME";
	
	private int channelId;
	private String channelName;
	
	public ServerChannel() {
		this(DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME);
	}
	
	public ServerChannel(int id, String name) {
		channelId = id;
		channelName = name;
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
	
	public void setChannelId(int value) {
		channelId = value;
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String value) {
		channelName = value;
	}
}
