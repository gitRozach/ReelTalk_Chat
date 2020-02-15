package network.ssl.server.manager.channelDatabase.items;

import java.util.Map;

import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabaseObject;

public abstract class Channel extends PropertyValueDatabaseObject {
	protected static final String CHANNEL_ID_PROPERTY_NAME = "channelId";
	protected static final int DEFAULT_CHANNEL_ID = -1;
	protected int channelId;
	
	public Channel() {
		this(DEFAULT_CHANNEL_ID);
	}
	
	public Channel(int id) {
		channelId = id;
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(CHANNEL_ID_PROPERTY_NAME)) {
			setChannelId(Integer.parseInt(propertyValueMap.get(CHANNEL_ID_PROPERTY_NAME)));
			++counter;
		}
		return counter;
	}
	
	@Override
	public String toDatabaseString() {
		return  CHANNEL_ID_PROPERTY_NAME + PROPERTY_START + getChannelId() + PROPERTY_END;
	}
	
	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int id) {
		channelId = id;
	}
}
