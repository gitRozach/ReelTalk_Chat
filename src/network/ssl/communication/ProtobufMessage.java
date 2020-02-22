package network.ssl.communication;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.google.protobuf.GeneratedMessageV3;

public class ProtobufMessage {
	protected SocketChannel socketChannel;
	protected GeneratedMessageV3 protobufMessage;
	
	public ProtobufMessage() {
		socketChannel = null;
		protobufMessage = null;
	}
	
	public ProtobufMessage(GeneratedMessageV3 message) {
		socketChannel = null;
		protobufMessage = message;
	}
	
	public ProtobufMessage(SelectionKey key, GeneratedMessageV3 message) {
		this((SocketChannel)key.channel(), message);
	}
	
	public ProtobufMessage(SocketChannel channel, GeneratedMessageV3 message) {
		socketChannel = channel;
		protobufMessage = message;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void putSocketChannel(SocketChannel channel) {
		socketChannel = channel;
	}

	public GeneratedMessageV3 getMessage() {
		return protobufMessage;
	}

	public void setProtobufMessage(GeneratedMessageV3 message) {
		protobufMessage = message;
	}
	
	public boolean hasSocketChannel() {
		return socketChannel != null;
	}
	
	public boolean hasMessage() {
		return protobufMessage != null;
	}
}
