package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.channelDatabase.items.ServerChannel;
import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabase;

public class ClientChannelManager extends PropertyValueDatabase<ServerChannel> {

	public ClientChannelManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ClientChannelManager(File databaseFile) throws IOException {
		super(ServerChannel.class, databaseFile);
	}	
}
