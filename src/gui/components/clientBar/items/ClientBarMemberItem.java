package gui.components.clientBar.items;

public abstract class ClientBarMemberItem implements ClientBarItem {
	protected int id;
	protected String username;
	
	public ClientBarMemberItem() {
		this(-1, "");
	}

	public ClientBarMemberItem(int id, String username) {
		this.id = id;
		this.username = username;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
