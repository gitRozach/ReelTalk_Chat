package network.peer.server.database.protobuf;

import java.io.IOException;

import protobuf.ClientMessages.ClientProfileComment;

public class ProfileCommentDatabase extends ProtobufFileDatabase<ClientProfileComment>{
	public ProfileCommentDatabase() throws IOException {
		super(ClientProfileComment.class);
	}
}
