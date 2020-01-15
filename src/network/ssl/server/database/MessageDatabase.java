package network.ssl.server.database;

import java.io.File;
import java.io.IOException;

import network.ssl.client.id.ClientData;

public class MessageDatabase extends ClientDatabase<ClientData> {

	public MessageDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath), true);
	}
	
	public MessageDatabase(String databaseFilePath, boolean initialize) throws IOException {
		this(new File(databaseFilePath), initialize);
	}
	
	public MessageDatabase(File databaseFile) throws IOException {
		this(databaseFile, false);
	}
	
	public MessageDatabase(File databaseFile, boolean initialize) throws IOException {
		super(databaseFile, initialize);
	}

}
