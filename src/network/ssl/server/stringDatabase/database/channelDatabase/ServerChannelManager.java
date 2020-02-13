package network.ssl.server.stringDatabase.database.channelDatabase;

import java.io.File;
import java.io.IOException;

import network.ssl.server.stringDatabase.database.propertyValueDatabase.PropertyValueDatabase;

public class ServerChannelManager extends PropertyValueDatabase<ServerChannel> {

	public ServerChannelManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ServerChannelManager(File databaseFile) throws IOException {
		super(ServerChannel.class, databaseFile);
	}	
}
