package gui.components.channelBar.channelBarItems;

public abstract class ChannelBarChannelItem implements ChannelBarItem {
	protected int channelId;
	protected String channelName;
	
	public ChannelBarChannelItem(int id, String name) {
		channelId = id;
		channelName = name;
	}
	
	public int getChannelId() {
		return channelId;
	}
	public void setChannelId(int id) {
		channelId = id;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String name) {
		channelName = name;
	}
}
