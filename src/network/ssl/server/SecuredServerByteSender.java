package network.ssl.server;

import java.nio.channels.SelectionKey;

public interface SecuredServerByteSender {
	public abstract void onBytesSent(SelectionKey clientKey, byte[] requestBytes);
}
