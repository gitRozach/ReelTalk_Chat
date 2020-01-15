package gui.client.components.channelBar.channelBarItems;

public abstract class ChannelBarClientItem implements ChannelBarItem {
	protected int clientId;
	protected String clientName;
	
	public ChannelBarClientItem(int id, String name) {
		this.clientId = id;
		this.clientName = name;
	}
	
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}	
}
