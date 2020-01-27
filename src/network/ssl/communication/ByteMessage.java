package network.ssl.communication;

import java.nio.channels.SelectionKey;

public class ByteMessage {
	private SelectionKey clientKey;
	private byte[] messageBytes;
	
	public ByteMessage() {
		this(null, null);
	}
	
	public ByteMessage(SelectionKey key, byte[] bytes) {
		clientKey = key;
		messageBytes = bytes;
	}

	public SelectionKey getClientKey() {
		return clientKey;
	}

	public void putClientKey(SelectionKey key) {
		clientKey = key;
	}

	public byte[] getMessageBytes() {
		return messageBytes;
	}

	public void putMessageBytes(byte[] bytes) {
		messageBytes = bytes;
	}
	
	
}
