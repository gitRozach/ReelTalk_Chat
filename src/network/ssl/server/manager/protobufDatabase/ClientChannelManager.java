package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;

import protobuf.ClientChannels.TextChannel;

public class ClientChannelManager extends ProtobufFileDatabase<TextChannel> {

	public ClientChannelManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ClientChannelManager(File databaseFile) throws IOException {
		super(databaseFile);
	}

	@Override
	public TextChannel readItem() {
		try {
			return TextChannel.parseDelimitedFrom(Channels.newInputStream(databaseChannel));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}	
}
