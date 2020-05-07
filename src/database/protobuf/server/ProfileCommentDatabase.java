package database.protobuf.server;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;
import protobuf.ClientMessages.ProfileComment;

public class ProfileCommentDatabase extends ProtobufFileDatabase<ProfileComment>{
	protected final String PROFILE_COMMENT_FILE_NAME_PREFIX = "profileComments";
	protected final String PROFILE_COMMENT_NAME_VALUE_SEPARATOR = "_";
	protected final String PROFILE_COMMENT_FILE_TYPE = ".txt";
	
	public ProfileCommentDatabase() throws IOException {
		this("", "");
	}
	
	public ProfileCommentDatabase(String databaseName) throws IOException	{
		this(databaseName, "");
	}
	
	public ProfileCommentDatabase(String databaseName, String filePath) throws IOException {
		super(ProfileComment.class, databaseName, filePath);
	}
	
	public String createProfileCommentFileNameFromId(int clientId) {
		return PROFILE_COMMENT_FILE_NAME_PREFIX + PROFILE_COMMENT_NAME_VALUE_SEPARATOR + clientId + PROFILE_COMMENT_FILE_TYPE;
	}
	
	@Override
	public void sort(List<ProfileComment> items) {
		items.sort(ClientProfileCommentComparator);
	}
	
	public static Comparator<ProfileComment> ClientProfileCommentComparator = new Comparator<ProfileComment>() {
		@Override
		public int compare(ProfileComment o1, ProfileComment o2) {
			return o1.getMessageBase().getMessageId() - o2.getMessageBase().getMessageId();
		}
	};
}
