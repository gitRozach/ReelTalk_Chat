package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import protobuf.ClientMessages.ChannelMessage;

public class ChannelMessageManager extends ProtobufFileDatabase<ChannelMessage>{	
	public ChannelMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public ChannelMessageManager(File databaseFile) throws IOException {
		super(ChannelMessage.class, databaseFile);
	}
	
	
}
