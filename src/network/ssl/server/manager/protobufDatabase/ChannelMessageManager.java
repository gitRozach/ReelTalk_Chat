package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import protobuf.ClientMessages.ChannelClientMessage;

public class ChannelMessageManager extends ProtobufFileDatabase<ChannelClientMessage>{	
	public ChannelMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public ChannelMessageManager(File databaseFile) throws IOException {
		super(databaseFile);
	}

	@Override
	public ChannelClientMessage readItem() {
		return null;
	}
}
