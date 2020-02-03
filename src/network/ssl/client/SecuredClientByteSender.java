package network.ssl.client;

public interface SecuredClientByteSender {
	public abstract void onBytesSent(byte[] plainBytes);
}
