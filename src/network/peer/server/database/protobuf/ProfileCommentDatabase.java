package network.peer.server.database.protobuf;

import java.io.File;
import java.io.IOException;

import protobuf.ClientMessages.ClientProfileComment;

public class ProfileCommentDatabase extends ProtobufFileDatabase<ClientProfileComment>{
	public ProfileCommentDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ProfileCommentDatabase(File databaseFile) throws IOException {
		super(ClientProfileComment.class, databaseFile);
	}
}
