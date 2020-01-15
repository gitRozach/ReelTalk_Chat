package network.ssl.client.handler;

public interface EventHandler {
	public abstract void handleEvent(byte[] eventBytes);
}
