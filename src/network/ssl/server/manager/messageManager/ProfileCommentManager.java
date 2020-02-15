package network.ssl.server.manager.messageManager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.messageManager.items.ProfileComment;

public class ProfileCommentManager extends MessageManager<ProfileComment>{
	public ProfileCommentManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ProfileCommentManager(File databaseFile) throws IOException {
		super(ProfileComment.class, databaseFile);
	}
}
