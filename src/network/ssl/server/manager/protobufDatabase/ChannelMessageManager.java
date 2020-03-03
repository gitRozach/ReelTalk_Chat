package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;

import protobuf.ClientMessages.ChannelMessage;

public class ChannelMessageManager extends ProtobufFileDatabase<ChannelMessage>{	
	public ChannelMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public ChannelMessageManager(File databaseFile) throws IOException {
		super(databaseFile);
	}

	@Override
	public ChannelMessage readItem() {
		try {
			return ChannelMessage.parseDelimitedFrom(Channels.newInputStream(databaseChannel));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
