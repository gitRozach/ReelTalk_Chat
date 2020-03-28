package network.peer.server.database.protobuf;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import protobuf.ClientMessages.ClientProfileComment;

public class ClientProfileCommentDatabase extends ProtobufFileDatabase<ClientProfileComment>{
	public ClientProfileCommentDatabase() throws IOException {
		super(ClientProfileComment.class);
	}
	
	public ClientProfileCommentDatabase(String filePath) throws IOException {
		super(ClientProfileComment.class, filePath);
	}
	
	@Override
	public void sort(List<ClientProfileComment> items) {
		Collections.sort(items, ClientProfileCommentComparator);
	}
	
	public static Comparator<ClientProfileComment> ClientProfileCommentComparator = new Comparator<ClientProfileComment>() {
		@Override
		public int compare(ClientProfileComment o1, ClientProfileComment o2) {
			return o1.getMessageBase().getMessageId() - o2.getMessageBase().getMessageId();
		}
	};
}
