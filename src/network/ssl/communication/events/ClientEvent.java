package network.ssl.communication.events;

import network.ssl.communication.MessagePacket;
 
public abstract class ClientEvent extends MessagePacket {
	private static final long serialVersionUID = 482615835534267198L;
	private long timestamp;
	
	public ClientEvent() {
		this.timestamp = System.currentTimeMillis();
	}
	
	public ClientEvent(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
