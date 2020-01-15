package network.ssl.communication.events;

public class ChannelDataEvent extends ClientEvent {
	private static final long serialVersionUID = -6031736836417958604L;
	private int channelId;
	private String channelName;
	private int[] userIds;

	public ChannelDataEvent() {
		super();
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

	public int[] getUserIds() {
		return userIds;
	}

	public void setUserIds(int[] userIds) {
		this.userIds = userIds;
	}	
}
