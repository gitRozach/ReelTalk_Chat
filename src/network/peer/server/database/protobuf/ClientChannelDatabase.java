package network.peer.server.database.protobuf;

import java.io.File;
import java.io.IOException;

import protobuf.ClientChannels.TextChannel;

public class ClientChannelDatabase extends ProtobufFileDatabase<TextChannel> {
		
	public ClientChannelDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ClientChannelDatabase(File databaseFile) throws IOException {
		super(TextChannel.class, databaseFile);
	}	
}
