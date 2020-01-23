package network.ssl.communication.events;

public class ChannelMessageEvent extends ClientEvent {
	private static final long serialVersionUID = 269486691458818300L;
	private int id;
	private String sender;
	private String message;
	
	public ChannelMessageEvent() {
		this(-1, "", "");
	}
	
	public ChannelMessageEvent(int messageId, String messageSender, String messageText) {
		super();
		id = messageId;
		sender = messageSender;
		message = messageText;
	}

	public int getId() {
		return id;
	}

	public void setId(int messageId) {
		id = messageId;
	}
	
	public String getSender() {
		return sender;
	}

	public void setSender(String messageSender){
		sender = messageSender;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String messageText) {
		message = messageText;
	}
}
