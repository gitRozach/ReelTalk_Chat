package network.ssl.communication.events;

import network.ssl.client.id.ClientProfileData;

public class ClientLoggedInEvent extends ClientEvent {
	private static final long serialVersionUID = 7043498716902054709L;
	private ClientProfileData profileData;
	
	public ClientLoggedInEvent() {
		this(null);
	}

	public ClientLoggedInEvent(ClientProfileData profileData) {
		super();
		this.profileData = profileData;
	}
	
	public ClientProfileData getProfileData() {
		return profileData;
	}

	public void setProfileData(ClientProfileData profileData) {
		this.profileData = profileData;
	}
}
