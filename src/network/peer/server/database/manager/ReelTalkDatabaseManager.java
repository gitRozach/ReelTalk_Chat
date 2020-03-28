package network.peer.server.database.manager;

import java.io.Closeable;
import java.io.IOException;

import network.peer.server.database.protobuf.ChannelMessageDatabase;
import network.peer.server.database.protobuf.ClientAccountDatabase;
import network.peer.server.database.protobuf.ClientChannelDatabase;
import network.peer.server.database.protobuf.ClientProfileCommentDatabase;
import network.peer.server.database.protobuf.PrivateMessageDatabase;

public class ReelTalkDatabaseManager implements Closeable {
	private ChannelMessageDatabase channelMessageDatabase;
	private PrivateMessageDatabase privateMessageDatabase;
	private ClientProfileCommentDatabase profileCommentDatabase;
	private ClientAccountDatabase clientAccountDatabase;
	private ClientChannelDatabase clientChannelDatabase;
	
	public ReelTalkDatabaseManager() throws IOException {
		channelMessageDatabase = new ChannelMessageDatabase();
		privateMessageDatabase = new PrivateMessageDatabase();
		profileCommentDatabase = new ClientProfileCommentDatabase();
		clientAccountDatabase = new ClientAccountDatabase();
		clientChannelDatabase = new ClientChannelDatabase();
	}
	
	@Override
	public void close() throws IOException {
		channelMessageDatabase.close();
		privateMessageDatabase.close();
		profileCommentDatabase.close();
		clientAccountDatabase.close();
		clientChannelDatabase.close();
	}
	
	public void loadItems() {
		channelMessageDatabase.loadFileItems();
		privateMessageDatabase.loadFileItems();
		profileCommentDatabase.loadFileItems();
		clientAccountDatabase.loadFileItems();
		clientChannelDatabase.loadFileItems();
	}
	
	public boolean configureChannelMessageDatabasePath(String newPath) {
		return channelMessageDatabase.changeDatabaseFile(newPath);
	}
	
	public boolean configurePrivateMessageDatabasePath(String newPath) {
		return privateMessageDatabase.changeDatabaseFile(newPath);
	}
	
	public boolean configureProfileCommentDatabasePath(String newPath) {
		return profileCommentDatabase.changeDatabaseFile(newPath);
	}
	
	public boolean configureClientAccountDatabasePath(String newPath) {
		return clientAccountDatabase.changeDatabaseFile(newPath);
	}
	
	public boolean configureClientChannelDatabasePath(String newPath) {
		return clientChannelDatabase.changeDatabaseFile(newPath);
	}
	
	/*
	 * 
	 */

	public ChannelMessageDatabase getChannelMessageDatabase() {
		return channelMessageDatabase;
	}

	public PrivateMessageDatabase getPrivateMessageDatabase() {
		return privateMessageDatabase;
	}

	public ClientProfileCommentDatabase getProfileCommentDatabase() {
		return profileCommentDatabase;
	}

	public ClientAccountDatabase getClientAccountDatabase() {
		return clientAccountDatabase;
	}

	public ClientChannelDatabase getClientChannelDatabase() {
		return clientChannelDatabase;
	}
}
