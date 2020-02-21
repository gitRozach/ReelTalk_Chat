package network.ssl.client.callbacks;

import network.ssl.communication.ByteMessage;

public interface PeerCallback {
	public void connectionLost(Throwable throwable);
	public void messageSent(ByteMessage message);
	public void messageReceived(ByteMessage message);
}
