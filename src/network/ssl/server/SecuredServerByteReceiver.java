package network.ssl.server;

import java.nio.channels.SelectionKey;

public interface SecuredServerByteReceiver {
	public abstract void onBytesReceived(SelectionKey clientKey, byte[] requestBytes);
}
