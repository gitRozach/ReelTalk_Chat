package network.ssl.server.manager.messageManager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.messageManager.items.PrivateMessage;

public class PrivateMessageManager extends MessageManager<PrivateMessage>{
	public PrivateMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public PrivateMessageManager(File databaseFile) throws IOException {
		super(PrivateMessage.class, databaseFile);
	}
}
