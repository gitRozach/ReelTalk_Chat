package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import protobuf.ClientChannels.TextChannel;

public class ClientChannelManager extends ProtobufFileDatabase<TextChannel> {

	public ClientChannelManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ClientChannelManager(File databaseFile) throws IOException {
		super(TextChannel.class, databaseFile);
	}	
}
