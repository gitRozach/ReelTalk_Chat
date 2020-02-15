package network.ssl.communication.events;

import network.ssl.communication.MessagePacket;
 
public abstract class ClientEvent extends MessagePacket {
	private static final long serialVersionUID = 482615835534267198L;
	protected int clientId;
	protected long timestamp;
	
	public ClientEvent() {
		this(-1);
	}
	
	public ClientEvent(int clientId) {
		this(clientId, System.currentTimeMillis());
	}
	
	public ClientEvent(int id, long eventTimestamp) {
		clientId = id;
		timestamp = eventTimestamp;
	}
	
	public int getClientId() {
		return clientId;
	}
	
	public void setClientId(int value) {
		clientId = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
