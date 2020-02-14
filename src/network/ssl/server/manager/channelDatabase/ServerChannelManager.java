package network.ssl.server.manager.channelDatabase;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabase;

public class ServerChannelManager extends PropertyValueDatabase<ServerChannel> {

	public ServerChannelManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ServerChannelManager(File databaseFile) throws IOException {
		super(ServerChannel.class, databaseFile);
	}	
}
