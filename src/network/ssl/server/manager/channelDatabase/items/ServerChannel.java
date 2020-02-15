package network.ssl.server.manager.channelDatabase.items;

import java.util.Map;

public class ServerChannel extends Channel {	
	public static final String CHANNEL_NAME_PROPERTY_NAME = "channelName";	
	public static final String DEFAULT_CHANNEL_NAME = "UNKNOWN_CHANNEL_NAME";
	private String channelName;
	
	public ServerChannel() {
		this(DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME);
	}
	
	public ServerChannel(int id, String name) {
		super(id);
		channelName = name;
	}
	
	@Override
	public String toDatabaseString() {
		return 	super.toDatabaseString() + 
				CHANNEL_NAME_PROPERTY_NAME + PROPERTY_START + getChannelName() + PROPERTY_END;
	}

	@Override
	public int initFromDatabaseString(String databaseString) {
		int superRes = super.initFromDatabaseString(databaseString);
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(CHANNEL_NAME_PROPERTY_NAME)) {
			setChannelName(propertyValueMap.get(CHANNEL_NAME_PROPERTY_NAME));
			++counter;
		}
		return superRes + counter;
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String value) {
		channelName = value;
	}
}
