package network.ssl.server.manager.messageManager.items;

public class ProfileComment extends PrivateMessage {
	public ProfileComment() {
		this(DEFAULT_MESSAGE_ID, DEFAULT_SENDER_NAME, DEFAULT_SENDER_ID, DEFAULT_MESSAGE_TEXT);
	}
	
	public ProfileComment(String databaseString) {
		super(databaseString);
		initFromDatabaseString(databaseString);
	}
	
	public ProfileComment(int id, String senderName, int senderId, String text) {
		this(id, senderName, senderId, text, DEFAULT_RECEIVER_ID, DEFAULT_RECEIVER_NAME);
	}
	
	public ProfileComment(	int id, String senderName, int senderId, String text, 
							int ReceiverId, String ReceiverName) {
		super(id, senderName, senderId, text);
		receiverId = ReceiverId;
		receiverName = ReceiverName;
	}
}
