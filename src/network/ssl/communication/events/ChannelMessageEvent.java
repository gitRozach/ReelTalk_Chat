package network.ssl.communication.events;

public class ChannelMessageEvent extends ClientEvent {
	private static final long serialVersionUID = 269486691458818300L;
	private int senderId;
	private String message;
	
	public ChannelMessageEvent() {
		this(-1, "");
	}
	
	public ChannelMessageEvent(int senderId, String message) {
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
