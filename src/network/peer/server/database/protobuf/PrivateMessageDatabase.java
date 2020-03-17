package network.peer.server.database.protobuf;

import java.io.IOException;

import protobuf.ClientMessages.PrivateMessage;

public class PrivateMessageDatabase extends ProtobufFileDatabase<PrivateMessage>{
	public PrivateMessageDatabase() throws IOException {
		super(PrivateMessage.class);
	}
}
