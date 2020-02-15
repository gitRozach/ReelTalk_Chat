package network.ssl.server.manager.messageManager.items;

import java.util.Map;

import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabaseObject;

public abstract class DatabaseMessage extends PropertyValueDatabaseObject {
	protected static final String MESSAGE_ID_PROPERTY_NAME = "messageId";
	protected static final int DEFAULT_MESSAGE_ID = -1;
	protected int messageId;
	
	public DatabaseMessage() {
		this(DEFAULT_MESSAGE_ID);
	}
	
	public DatabaseMessage(String databaseString) {
		initFromDatabaseString(databaseString);
	}
	
	public DatabaseMessage(int id) {
		messageId = id;
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(MESSAGE_ID_PROPERTY_NAME)) {
			setMessageId(Integer.parseInt(propertyValueMap.get(MESSAGE_ID_PROPERTY_NAME)));
			++counter;
		}
		return counter;
	}
	
	@Override
	public String toDatabaseString() {
		return  MESSAGE_ID_PROPERTY_NAME + PROPERTY_START + getMessageId() + PROPERTY_END;
	}
	
	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int id) {
		messageId = id;
	}
}
