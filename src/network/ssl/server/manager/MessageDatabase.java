package network.ssl.server.manager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.stringDatabase.database.propertyValueDatabase.PropertyValueDatabase;
import network.ssl.server.stringDatabase.database.propertyValueDatabase.messages.Message;

public class MessageDatabase extends PropertyValueDatabase<Message> {
	public MessageDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public MessageDatabase(File databaseFile) throws IOException {
		super(Message.class, databaseFile);
	}

}
