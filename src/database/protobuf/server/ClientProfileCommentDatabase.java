package database.protobuf.server;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;
import protobuf.ClientMessages.ClientProfileComment;

public class ClientProfileCommentDatabase extends ProtobufFileDatabase<ClientProfileComment>{
	protected final String PROFILE_COMMENT_FILE_NAME_PREFIX = "profileComments";
	protected final String PROFILE_COMMENT_NAME_VALUE_SEPARATOR = "_";
	protected final String PROFILE_COMMENT_FILE_TYPE = ".txt";
	
	public ClientProfileCommentDatabase() throws IOException {
		this("", "");
	}
	
	public ClientProfileCommentDatabase(String databaseName) throws IOException	{
		this(databaseName, "");
	}
	
	public ClientProfileCommentDatabase(String databaseName, String filePath) throws IOException {
		super(ClientProfileComment.class, databaseName, filePath);
	}
	
	public String createProfileCommentFileNameFromId(int clientId) {
		return PROFILE_COMMENT_FILE_NAME_PREFIX + PROFILE_COMMENT_NAME_VALUE_SEPARATOR + clientId + PROFILE_COMMENT_FILE_TYPE;
	}
	
	@Override
	public void sort(List<ClientProfileComment> items) {
		items.sort(ClientProfileCommentComparator);
	}
	
	public static Comparator<ClientProfileComment> ClientProfileCommentComparator = new Comparator<ClientProfileComment>() {
		@Override
		public int compare(ClientProfileComment o1, ClientProfileComment o2) {
			return o1.getMessageBase().getMessageId() - o2.getMessageBase().getMessageId();
		}
	};
}
