package network.ssl.server.manager.messageManager.items;

import java.util.Map;

import network.ssl.server.manager.messageManager.Message;

public class PrivateMessage extends Message {
	protected static final String RECEIVER_ID_PROPERTY_NAME = "receiverId";
	protected static final String RECEIVER_NAME_PROPERTY_NAME = "receiverName";
	
	protected static final int DEFAULT_RECEIVER_ID = -1;
	protected static final String DEFAULT_RECEIVER_NAME = "UNKNOWN_RECEIVER_NAME";
	
	protected int receiverId;
	protected String receiverName;
	
	public PrivateMessage() {
		this(DEFAULT_MESSAGE_ID, DEFAULT_SENDER_NAME, DEFAULT_SENDER_ID, DEFAULT_MESSAGE_TEXT);
	}
	
	public PrivateMessage(int messageId, String messageSenderName, int messageSenderId, String messageText) {
		this(messageId, messageSenderName, messageSenderId, messageText, DEFAULT_RECEIVER_ID, DEFAULT_RECEIVER_NAME);
	}
	
	public PrivateMessage(	int messageId, String messageSenderName, int messageSenderId, String messageText, 
							int messageReceiverId, String messageReceiverName) {
		super(messageId, messageSenderName, messageSenderId, messageText);
		receiverId = messageReceiverId;
		receiverName = messageReceiverName;
	}

	@Override
	public String toDatabaseString() {
		return 	super.toDatabaseString() + 
				RECEIVER_ID_PROPERTY_NAME + PROPERTY_START + getReceiverId() + PROPERTY_END +
				RECEIVER_NAME_PROPERTY_NAME + PROPERTY_START + getReceiverName() + PROPERTY_END;
	}

	@Override
	public int initFromDatabaseString(String databaseString) {
		int superRes = super.initFromDatabaseString(databaseString);
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(RECEIVER_ID_PROPERTY_NAME)) {
			setReceiverId(Integer.parseInt(propertyValueMap.get(RECEIVER_ID_PROPERTY_NAME)));
			++counter;
		}
		if(propertyValueMap.containsKey(RECEIVER_NAME_PROPERTY_NAME)) {
			setReceiverName(propertyValueMap.get(RECEIVER_NAME_PROPERTY_NAME));
			++counter;
		}
		return superRes + counter;
	}
	
	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	
	public String getReceiverName() {
		return receiverName;
	}
	
	public void setReceiverName(String value) {
		receiverName = value;
	}
}
