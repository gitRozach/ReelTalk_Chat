package network.ssl.communication;

import java.nio.channels.SocketChannel;

public class ByteMessage {
	protected SocketChannel socketChannel;
	protected byte[] messageBytes;
	
	public ByteMessage() {
		this(null, null);
	}
	
	public ByteMessage(byte[] bytes) {
		this(null, bytes);
	}
	
	public ByteMessage(SocketChannel channel, byte[] bytes) {
		socketChannel = channel;
		messageBytes = bytes;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void putSocketChannel(SocketChannel channel) {
		socketChannel = channel;
	}

	public byte[] getMessageBytes() {
		return messageBytes;
	}

	public void putMessageBytes(byte[] bytes) {
		messageBytes = bytes;
	}
	
	public boolean hasSocketChannel() {
		return socketChannel != null;
	}
	
	public boolean hasMessageBytes() {
		return messageBytes != null;
	}
}
