package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;

import protobuf.ClientMessages.PrivateMessage;

public class PrivateMessageManager extends ProtobufFileDatabase<PrivateMessage>{
	public PrivateMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public PrivateMessageManager(File databaseFile) throws IOException {
		super(databaseFile);
	}

	@Override
	public PrivateMessage readItem() {
		try {
			return PrivateMessage.parseDelimitedFrom(Channels.newInputStream(databaseChannel));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
