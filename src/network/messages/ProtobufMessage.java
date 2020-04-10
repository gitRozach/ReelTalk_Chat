package network.messages;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

public class ProtobufMessage {
	protected SocketChannel socketChannel;
	protected Message protobufMessage;
	
	public ProtobufMessage() {
		socketChannel = null;
		protobufMessage = null;
	}
	
	public ProtobufMessage(Message message) {
		socketChannel = null;
		protobufMessage = message;
	}
	
	public ProtobufMessage(SelectionKey key, Message message) {
		this((SocketChannel)key.channel(), message);
	}
	
	public ProtobufMessage(SocketChannel channel, Message message) {
		socketChannel = channel;
		protobufMessage = message;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel channel) {
		socketChannel = channel;
	}

	public Message getMessage() {
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
