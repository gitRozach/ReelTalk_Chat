package gui.components.channelBar.channelBarItems;

public abstract class ChannelBarClientItem implements ChannelBarItem {
	protected int clientId;
	protected String clientName;
	
	public ChannelBarClientItem(int id, String name) {
		clientId = id;
		clientName = name;
	}
	
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int id) {
		clientId = id;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String name) {
		clientName = name;
	}	
}
