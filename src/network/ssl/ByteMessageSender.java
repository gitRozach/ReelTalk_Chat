package network.ssl;

import network.ssl.communication.ByteMessage;

public interface ByteMessageSender {
	public abstract void onBytesSent(ByteMessage byteMessage);
}
