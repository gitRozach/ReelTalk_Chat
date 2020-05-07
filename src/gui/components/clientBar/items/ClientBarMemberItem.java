package gui.components.clientBar.items;

import protobuf.ClientIdentities.ClientStatus;

public abstract class ClientBarMemberItem implements ClientBarItem {
	protected int clientId;
	protected String clientUsername;
	protected String clientProfilePicture;
	protected ClientStatus clientStatus;
	protected boolean blocked;
	protected boolean friend;
	
	public ClientBarMemberItem() {
		this(-1, "", "/resources/icons/member.png", ClientStatus.OFFLINE, false, false);
	}
	
	public ClientBarMemberItem(int id, String username) {
		this(id, username, "/resources/icons/member.png", ClientStatus.OFFLINE, false, false);
	}
	
	public ClientBarMemberItem(int id, String username, String profilePicture, ClientStatus status) {
		this(id, username, profilePicture, status, false, false);
	}
	
	public ClientBarMemberItem(int id, String username, String profilePicture, ClientStatus status, boolean isFriend) {
		this(id, username, profilePicture, status, false, isFriend);
	}

	public ClientBarMemberItem(int id, String username, String profilePicture, ClientStatus status, boolean isBlocked, boolean isFriend) {
		clientId = id;
		clientUsername = username;
		clientProfilePicture = profilePicture;
		clientStatus = status;
		blocked = isBlocked;
		friend = isFriend;
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
	
	public String getProfilePicture() {
		return clientProfilePicture;
	}
	
	public void setProfilePicture(String path) {
		clientProfilePicture = path;
	}
	
	public ClientStatus getClientStatus() {
		return clientStatus;
	}
	
	public void setClientStatus(ClientStatus status) {
		clientStatus = status;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	public void setBlocked(boolean value) {
		blocked = value;
	}
	
	public boolean isFriend() {
		return friend;
	}
	
	public void setFriend(boolean value) {
		friend = value;
	}
}
