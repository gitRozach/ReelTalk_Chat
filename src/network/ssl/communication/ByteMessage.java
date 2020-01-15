package network.ssl.communication;

import java.nio.channels.SelectionKey;

public class ByteMessage {
	private SelectionKey clientKey;
	private byte[] messageBytes;
	
	public ByteMessage() {
		this(null, null);
	}
	
	public ByteMessage(SelectionKey clientKey, byte[] messageBytes) {
		this.clientKey = clientKey;
		this.messageBytes = messageBytes;
	}

	public SelectionKey getClientKey() {
		return clientKey;
	}

	public void putClientKey(SelectionKey clientKey) {
		this.clientKey = clientKey;
	}

	public byte[] getMessageBytes() {
		return messageBytes;
	}

	public void putMessageBytes(byte[] messageBytes) {
		this.messageBytes = messageBytes;
	}
	
	
}
