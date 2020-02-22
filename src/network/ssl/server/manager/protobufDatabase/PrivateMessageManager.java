package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import protobuf.ClientMessages.PrivateClientMessage;

public class PrivateMessageManager extends ProtobufFileDatabase<PrivateClientMessage>{
	public PrivateMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public PrivateMessageManager(File databaseFile) throws IOException {
		super(databaseFile);
	}

	@Override
	public PrivateClientMessage readItem() {
		return null;
	}
}
