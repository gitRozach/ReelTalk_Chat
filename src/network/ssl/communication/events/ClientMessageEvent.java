package network.ssl.communication.events;

public abstract class ClientMessageEvent extends ClientEvent {
	private static final long serialVersionUID = -6308067959933319248L;
	protected int messageId;
	protected String senderName;
	protected String messageText;
	
	public ClientMessageEvent() {
		this(-1, -1, "", "");
	}
	
	public ClientMessageEvent(int clientId, int _messageId, String _senderName, String _messageText) {
		super(clientId);
		messageId = _messageId;
		senderName = _senderName;
		messageText = _messageText;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
}
