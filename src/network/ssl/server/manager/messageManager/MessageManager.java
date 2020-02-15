package network.ssl.server.manager.messageManager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.messageManager.items.DatabaseMessage;
import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabase;

public class MessageManager<T extends DatabaseMessage> extends PropertyValueDatabase<T> {
	public MessageManager(Class<T> itemClass, String databaseFilePath) throws IOException {
		this(itemClass, new File(databaseFilePath));
	}

	public MessageManager(Class<T> itemClass, File databaseFile) throws IOException {
		super(itemClass, databaseFile);
	}
}
