package gui.client.components.channelBar.channelBarItems;

public abstract class ChannelBarChannelItem implements ChannelBarItem {
	protected int channelId;
	protected String channelName;
	
	public ChannelBarChannelItem(int id, String name) {
		this.channelId = id;
		this.channelName = name;
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
