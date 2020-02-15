package network.ssl.communication.events;

public class ChannelMessageEvent extends ClientMessageEvent {
	private static final long serialVersionUID = 269486691458818300L;
	protected int channelId;
	protected String channelName;
	
	public ChannelMessageEvent() {
		this(-1, -1, "", "", -1, "");
	}
	
	public ChannelMessageEvent(	int senderId, int messageId, String senderName, String messageText, 
								int _channelId, String _channelName) {
		super(senderId, messageId, senderName, messageText);
		channelId = _channelId;
		channelName = _channelName;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
