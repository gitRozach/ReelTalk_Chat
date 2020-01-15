package network.ssl.communication.events;

import network.ssl.client.id.ClientAccountData;

public class AccountDataEvent extends ClientEvent {
	private static final long serialVersionUID = -2372154955431108505L;
	private ClientAccountData clientAccountData;
	
	public AccountDataEvent() {
		this(null);
	}
	
	public AccountDataEvent(ClientAccountData data) {
		super();
		this.clientAccountData = data;
	}
	
	public void setClientAccountData(ClientAccountData data) {
		this.clientAccountData = data;
	}

	public ClientAccountData getClientAccountData() {
		return clientAccountData;
	}
}
