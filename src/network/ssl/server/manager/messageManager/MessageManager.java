package network.ssl.server.manager.messageManager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabase;

public class MessageManager extends PropertyValueDatabase<Message> {
	public MessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public MessageManager(File databaseFile) throws IOException {
		super(Message.class, databaseFile);
	}

}
