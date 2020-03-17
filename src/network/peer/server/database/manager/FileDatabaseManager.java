package network.peer.server.database.manager;

import java.io.IOException;

import network.peer.server.database.protobuf.ChannelMessageDatabase;
import network.peer.server.database.protobuf.ClientAccountDatabase;
import network.peer.server.database.protobuf.ClientChannelDatabase;
import network.peer.server.database.protobuf.PrivateMessageDatabase;
import network.peer.server.database.protobuf.ProfileCommentDatabase;

public class FileDatabaseManager {
	private ChannelMessageDatabase channelMessageDatabase;
	private PrivateMessageDatabase privateMessageDatabase;
	private ProfileCommentDatabase profileCommentDatabase;
	
	private ClientAccountDatabase clientAccountDatabase;
	private ClientChannelDatabase clientChannelDatabase;
	
	public FileDatabaseManager() throws IOException {
		channelMessageDatabase = new ChannelMessageDatabase();
		privateMessageDatabase = new PrivateMessageDatabase();
		profileCommentDatabase = new ProfileCommentDatabase();
		
		clientAccountDatabase = new ClientAccountDatabase();
		clientChannelDatabase = new ClientChannelDatabase();
	}
	
	

	public ChannelMessageDatabase getChannelMessageDatabase() {
		return channelMessageDatabase;
	}

	public PrivateMessageDatabase getPrivateMessageDatabase() {
		return privateMessageDatabase;
	}

	public ProfileCommentDatabase getProfileCommentDatabase() {
		return profileCommentDatabase;
	}

	public ClientAccountDatabase getClientAccountDatabase() {
		return clientAccountDatabase;
	}

	public ClientChannelDatabase getClientChannelDatabase() {
		return clientChannelDatabase;
	}
}
