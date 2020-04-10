package network.peer.server.manager;

import java.io.IOException;

import database.protobuf.manager.ProtobufFileDatabaseManager;
import database.protobuf.server.ChannelMessageDatabase;
import database.protobuf.server.ClientAccountDatabase;
import database.protobuf.server.ClientChannelDatabase;
import database.protobuf.server.ClientProfileCommentDatabase;
import database.protobuf.server.PrivateMessageDatabase;

public class ReelTalkServerDatabaseManager extends ProtobufFileDatabaseManager {	
	protected ClientAccountDatabase clientAccountDatabase;
	protected ClientChannelDatabase clientChannelDatabase;
	protected ChannelMessageDatabase channelMessageDatabase;
	protected PrivateMessageDatabase privateMessageDatabase;
	protected ClientProfileCommentDatabase profileCommentDatabase;
	
	protected final String CLIENT_ACCOUNT_DATABASE_NAME = "clientAccountDatabase";
	protected final String CLIENT_CHANNEL_DATABASE_NAME = "clientChannelDatabase";
	protected final String CHANNEL_MESSAGE_DATABASE_NAME = "channelMessageDatabase";
	protected final String PRIVATE_MESSAGE_DATABASE_NAME = "privateMessageDatabase";
	protected final String PROFILE_COMMENT_DATABASE_NAME = "profileCommentDatabase";

	public ReelTalkServerDatabaseManager() throws IOException {
		super();
		initDatabases();
		registerDatabases();
	}
	
	private void initDatabases() {
		try {
			clientAccountDatabase = new ClientAccountDatabase(CLIENT_ACCOUNT_DATABASE_NAME);
			clientChannelDatabase = new ClientChannelDatabase(CLIENT_CHANNEL_DATABASE_NAME);
			channelMessageDatabase = new ChannelMessageDatabase(CHANNEL_MESSAGE_DATABASE_NAME);
			privateMessageDatabase = new PrivateMessageDatabase(PRIVATE_MESSAGE_DATABASE_NAME);
			profileCommentDatabase = new ClientProfileCommentDatabase(PROFILE_COMMENT_DATABASE_NAME);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void registerDatabases() {
		registerDatabase(clientAccountDatabase);
		registerDatabase(clientChannelDatabase);
		registerDatabase(channelMessageDatabase);
		registerDatabase(privateMessageDatabase);
		registerDatabase(profileCommentDatabase);
	}
	
	public ClientAccountDatabase getClientAccountDatabase() {
		return clientAccountDatabase;
	}
	
	public ClientChannelDatabase getClientChannelDatabase() {
		return clientChannelDatabase;
	}
	
	public ChannelMessageDatabase getChannelMessageDatabase() {
		return channelMessageDatabase;
	}
	
	public PrivateMessageDatabase getPrivateMessageDatabase() {
		return privateMessageDatabase;
	}
	
	public ClientProfileCommentDatabase getProfileCommentDatabase() {
		return profileCommentDatabase;
	}
}
