package network.ssl.communication.events;

public class PrivateMessageEvent extends ClientEvent {
	private static final long serialVersionUID = 6039751109808096677L;
	private int senderId;
	private String message;
	
	public PrivateMessageEvent() {
		this(-1, "");
	}
	
	public PrivateMessageEvent(int senderId, String message) {
		super();
		this.senderId = senderId;
		this.message = message;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
