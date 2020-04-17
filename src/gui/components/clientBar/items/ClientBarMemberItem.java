package gui.components.clientBar.items;

public abstract class ClientBarMemberItem implements ClientBarItem {
	protected int clientId;
	protected String clientUsername;
	
	public ClientBarMemberItem() {
		this(-1, "");
	}

	public ClientBarMemberItem(int id, String username) {
		clientId = id;
		clientUsername = username;
	}
	
	public int getId() {
		return clientId;
	}

	public void setId(int id) {
		clientId = id;
	}

	public String getUsername() {
		return clientUsername;
	}

	public void setUsername(String username) {
		clientUsername = username;
	}
}
