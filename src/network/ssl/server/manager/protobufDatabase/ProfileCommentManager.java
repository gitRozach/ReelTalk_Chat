package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import protobuf.ClientMessages.ProfileComment;

public class ProfileCommentManager extends ProtobufFileDatabase<ProfileComment>{
	public ProfileCommentManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ProfileCommentManager(File databaseFile) throws IOException {
		super(databaseFile);
	}

	@Override
	public ProfileComment readItem() {
		return null;
	}
}
