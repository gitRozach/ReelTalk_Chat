package network.ssl.server.handler;

import java.nio.channels.SelectionKey;

public interface RequestHandler {
	public abstract void handleRequest(SelectionKey clientKey, byte[] requestBytes);
}
