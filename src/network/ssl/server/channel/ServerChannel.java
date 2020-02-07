package network.ssl.server.channel;

import network.ssl.server.database.DatabaseObject;

public class ServerChannel implements DatabaseObject {
	public static final String PROPERTY_START = "=";
	public static final String PROPERTY_END = "#";
	
	public static final String ID_PROPERTY_NAME = "id";
	public static final String NAME_PROPERTY_NAME = "name";
	
	public static final int DEFAULT_ID = -1;
	public static final String DEFAULT_NAME = "UNDEFINED_NAME";
	
	private int id;
	private String name;
	
	public ServerChannel() {
		this(DEFAULT_ID, DEFAULT_NAME);
	}
	
	public ServerChannel(int channelId, String channelName) {
		id = channelId;
		name = channelName;
	}
	
	@Override
	public String toDatabaseString() {
		return (ID_PROPERTY_NAME + PROPERTY_START + getId() + PROPERTY_END +
				NAME_PROPERTY_NAME + PROPERTY_START + getName() + PROPERTY_END).trim();
	}

	@Override
	public int initFromDatabaseString(String databaseString) {
		String[] namesAndValues = databaseString.split(PROPERTY_END);
		int propSetCounter = 0;
		
		for(String current : namesAndValues) {
			try {
				String currentSplit[] = current.split(PROPERTY_START);
				String propName = currentSplit[0];
				String propValue = currentSplit[1];
				
				if(propName.equals(ID_PROPERTY_NAME)) {
					setId(Integer.parseInt(propValue));
					++propSetCounter;
				}
				else if(propName.equals(NAME_PROPERTY_NAME)) {
					setName(propValue);
					++propSetCounter;
				}
			}
			catch(Exception e) {
				continue;
			}
		}
		return propSetCounter;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int value) {
		id = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String value) {
		name = value;
	}
}
