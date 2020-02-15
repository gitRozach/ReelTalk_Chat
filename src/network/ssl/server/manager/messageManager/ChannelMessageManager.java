package network.ssl.server.manager.messageManager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.messageManager.items.ChannelMessage;

public class ChannelMessageManager extends MessageManager<ChannelMessage>{	
	public ChannelMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public ChannelMessageManager(File databaseFile) throws IOException {
		super(ChannelMessage.class, databaseFile);
	}
}
