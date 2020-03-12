package network.peer.server.database.protobuf;

import java.io.File;
import java.io.IOException;

import protobuf.ClientMessages.PrivateMessage;

public class PrivateMessageDatabase extends ProtobufFileDatabase<PrivateMessage>{
	public PrivateMessageDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public PrivateMessageDatabase(File databaseFile) throws IOException {
		super(PrivateMessage.class, databaseFile);
	}
}
