package network.ssl.client;

public interface SecuredClientByteReceiver {
	public abstract void onBytesReceived(byte[] plainBytes);
}
