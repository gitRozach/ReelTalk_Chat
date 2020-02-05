package network.ssl;

import network.ssl.communication.ByteMessage;

public interface ByteMessageReceiver {
	public abstract void onBytesReceived(ByteMessage byteMessage);
}
