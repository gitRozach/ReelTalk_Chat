package network.ssl.server.manager.messageManager;

import java.util.Map;

import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabaseObject;

public abstract class Message extends PropertyValueDatabaseObject {
	
	protected static final String MESSAGE_ID_PROPERTY_NAME = "messageId";
	protected static final String SENDER_NAME_PROPERTY_NAME = "senderName";
	protected static final String SENDER_ID_PROPERTY_NAME = "senderId";
	protected static final String MESSAGE_TEXT_PROPERTY_NAME = "messageText";
	
	protected static final int DEFAULT_MESSAGE_ID = -1;
	protected static final String DEFAULT_SENDER_NAME = "UNKNOWN_SENDER_NAME";
	protected static final int DEFAULT_SENDER_ID = -1;
	protected static final String DEFAULT_MESSAGE_TEXT = "UNKNOWN_MESSAGE_TEXT";
	
	protected int messageId;
	protected String senderName;
	protected int senderId;
	protected String messageText;
	
	public Message() {
		this(DEFAULT_MESSAGE_ID, DEFAULT_SENDER_NAME, DEFAULT_SENDER_ID, DEFAULT_MESSAGE_TEXT);
	}
	
	public Message(int id, String messageSenderName, int messageSenderId, String text) {
		messageId = id;
		senderName = messageSenderName;
		senderId = messageSenderId;
		messageText = text;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		if(!(other instanceof Message))
			return false;
		if(this == other)
			return true;
		Message otherMessage = (Message)other;
		return 	getMessageId() == otherMessage.getMessageId() &&
				getSenderName().equals(otherMessage.getSenderName()) &&
				getSenderId() == otherMessage.getSenderId() &&
				getMessageText().equals(otherMessage.getMessageText());
	}

	@Override
	public String toDatabaseString() {
		return 	MESSAGE_ID_PROPERTY_NAME + PROPERTY_START + getMessageId() + PROPERTY_END +
				SENDER_NAME_PROPERTY_NAME + PROPERTY_START + getSenderName() + PROPERTY_END +
				SENDER_ID_PROPERTY_NAME + PROPERTY_START + getSenderId() + PROPERTY_END +
				MESSAGE_TEXT_PROPERTY_NAME + PROPERTY_START + getMessageText() + PROPERTY_END;
	}

	@Override
	public int initFromDatabaseString(String databaseString) {
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(MESSAGE_ID_PROPERTY_NAME)) {
			setMessageId(Integer.parseInt(propertyValueMap.get(MESSAGE_ID_PROPERTY_NAME)));
			++counter;
		}
		if(propertyValueMap.containsKey(SENDER_NAME_PROPERTY_NAME)) {
			setSenderName(propertyValueMap.get(SENDER_NAME_PROPERTY_NAME));
			++counter;
		}
		if(propertyValueMap.containsKey(SENDER_ID_PROPERTY_NAME)) {
			setSenderId(Integer.parseInt(propertyValueMap.get(SENDER_ID_PROPERTY_NAME)));
			++counter;
		}
		if(propertyValueMap.containsKey(MESSAGE_TEXT_PROPERTY_NAME)) {
			setMessageText(propertyValueMap.get(MESSAGE_TEXT_PROPERTY_NAME));
			++counter;
		}
		return counter;
	}
	
	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int id) {
		messageId = id;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String messageSenderName) {
		senderName = messageSenderName;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int messageSenderId) {
		senderId = messageSenderId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String text) {
		messageText = text;
	}
}
