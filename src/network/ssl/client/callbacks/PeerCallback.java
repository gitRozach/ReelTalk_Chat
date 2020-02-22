package network.ssl.client.callbacks;

import network.ssl.communication.ProtobufMessage;

public interface PeerCallback {
	public void connectionLost(Throwable throwable);
	public void messageSent(ProtobufMessage message);
	public void messageReceived(ProtobufMessage message);
}
