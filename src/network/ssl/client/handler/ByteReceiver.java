package network.ssl.client.handler;

public interface ByteReceiver {
	public abstract void onBytesReceived(byte[] receptionBytes);
}
