package network.peer.client.callbacks;

import network.messages.ProtobufMessage;

public interface PeerCallback {
	public void connectionLost(Throwable throwable);
	public void messageSent(ProtobufMessage message);
	public void messageReceived(ProtobufMessage message);
}
