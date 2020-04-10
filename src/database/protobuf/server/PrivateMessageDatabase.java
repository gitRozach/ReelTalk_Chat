package database.protobuf.server;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;
import protobuf.ClientMessages.PrivateMessage;
import utils.DirectoryUtils;

public class PrivateMessageDatabase extends ProtobufFileDatabase<PrivateMessage>{
	protected final String PRIVATE_MESSAGE_FILE_NAME_PREFIX = "privateMessages";
	protected final String PRIVATE_MESSAGE_NAME_VALUE_SEPARATOR = "_";
	protected final String PRIVATE_MESSAGE_FILE_TYPE = ".txt";
	
	protected static final String CLIENT_PREFIX = "client";
	protected static final String SEPARATOR = "_";
	protected static final String FILE_ENDING = ".pdb";
		
	public PrivateMessageDatabase() throws IOException {
		this("", "");
	}
	
	public PrivateMessageDatabase(String databaseName) throws IOException {
		this(databaseName, "");
	}
	
	public PrivateMessageDatabase(String databaseName, String filePath) throws IOException {
		super(PrivateMessage.class, databaseName, filePath);
	}
	
	public String createPrivateMessageFileNameFromIds(int clientIdOne, int clientIdTwo) {
		return PRIVATE_MESSAGE_FILE_NAME_PREFIX + 	PRIVATE_MESSAGE_NAME_VALUE_SEPARATOR + clientIdOne + 
													PRIVATE_MESSAGE_NAME_VALUE_SEPARATOR + clientIdTwo + PRIVATE_MESSAGE_FILE_TYPE;
	}
	
	@Override
	public void sort(List<PrivateMessage> items) {
		items.sort(PrivateMessageComparator);
	}
	
	protected String createFileNameFrom(int clientIdOne, int clientIdTwo) {
		return CLIENT_PREFIX + clientIdOne + SEPARATOR + CLIENT_PREFIX + clientIdTwo + FILE_ENDING;
	}
	
	protected String createFilePathFrom(String directoryPath, int clientIdOne, int clientIdTwo) {
		return directoryPath + (directoryPath.endsWith(File.pathSeparator) ? "" : File.pathSeparator) + createFileNameFrom(clientIdOne, clientIdTwo);
	}
	
	public boolean conversationExistsBetween(String directoryPath, int clientIdOne, int clientIdTwo) {
		if(directoryPath == null)
			return false;
		String pathPossibilityOne = createFilePathFrom(directoryPath, clientIdOne, clientIdTwo);
		String pathPossibilityTwo = createFilePathFrom(directoryPath, clientIdTwo, clientIdOne);
		return DirectoryUtils.fileExistsAndIsAccessible(pathPossibilityOne) || DirectoryUtils.fileExistsAndIsAccessible(pathPossibilityTwo);
	}
	
	public List<PrivateMessage> loadConversationBetween(String directoryPath, int clientIdOne, int clientIdTwo) {
		if(directoryPath == null)
			return null;
		String pathPossibilityOne = createFilePathFrom(directoryPath, clientIdOne, clientIdTwo);
		String pathPossibilityTwo = createFilePathFrom(directoryPath, clientIdTwo, clientIdOne);
		if(!setDatabaseFile(pathPossibilityOne) && !setDatabaseFile(pathPossibilityTwo))
			return null;
		List<PrivateMessage> messages = null;
		return messages;
	}
	
	public static Comparator<PrivateMessage> PrivateMessageComparator = new Comparator<PrivateMessage>() {
		@Override
		public int compare(PrivateMessage o1, PrivateMessage o2) {
			return o1.getMessageBase().getMessageId() - o2.getMessageBase().getMessageId();
		}
	};
}
