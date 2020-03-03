package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;

import protobuf.ClientMessages.ClientProfileComment;

public class ProfileCommentManager extends ProtobufFileDatabase<ClientProfileComment>{
	public ProfileCommentManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ProfileCommentManager(File databaseFile) throws IOException {
		super(databaseFile);
	}

	@Override
	public ClientProfileComment readItem() {
		try {
			return ClientProfileComment.parseDelimitedFrom(Channels.newInputStream(databaseChannel));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
