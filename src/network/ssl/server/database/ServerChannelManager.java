package network.ssl.server.database;

import java.io.File;
import java.io.IOException;

import network.ssl.server.channel.ServerChannel;

public class ServerChannelManager extends Database<ServerChannel> {

	public ServerChannelManager(String databaseFilePath) throws IOException {
		super(ServerChannel.class, databaseFilePath);
	}
	
	public ServerChannelManager(String databaseFilePath, boolean initFromFile) throws IOException {
		super(ServerChannel.class, databaseFilePath, initFromFile);
	}
	
	public ServerChannelManager(File databaseFile) throws IOException {
		super(ServerChannel.class, databaseFile);
	}
	
	public ServerChannelManager(File databaseFile, boolean initFromFile) throws IOException {
		super(ServerChannel.class, databaseFile, initFromFile);
	}
	
	public int initialize() throws IOException {
		int initRes = super.initialize();
		System.out.println(((ServerChannel)getDatabaseItem(0)).getName());
		System.out.println(((ServerChannel)getDatabaseItem(1)).getName());
		System.out.println(((ServerChannel)getDatabaseItem(2)).getName());
		System.out.println(((ServerChannel)getDatabaseItem(3)).getName());
		System.out.println(((ServerChannel)getDatabaseItem(4)).getName());
		System.out.println(((ServerChannel)getDatabaseItem(5)).getName());
		System.out.println(((ServerChannel)getDatabaseItem(6)).getName());
		return initRes;
	}
	
}
