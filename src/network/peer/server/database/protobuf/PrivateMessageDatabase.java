package network.peer.server.database.protobuf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import protobuf.ClientMessages.PrivateMessage;

public class PrivateMessageDatabase extends ProtobufFileDatabase<PrivateMessage>{
	protected static final String CLIENT_PREFIX = "client";
	protected static final String SEPARATOR = "_";
	protected static final String FILE_ENDING = ".pdb";
		
	public PrivateMessageDatabase() throws IOException {
		super(PrivateMessage.class);
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
		return fileExistsAndIsAccessible(pathPossibilityOne) || fileExistsAndIsAccessible(pathPossibilityTwo);
	}
	
	public List<PrivateMessage> loadConversationBetween(String directoryPath, int clientIdOne, int clientIdTwo) {
		if(directoryPath == null)
			return null;
		String pathPossibilityOne = createFilePathFrom(directoryPath, clientIdOne, clientIdTwo);
		String pathPossibilityTwo = createFilePathFrom(directoryPath, clientIdTwo, clientIdOne);
		if(!changeDatabaseFile(pathPossibilityOne) && !changeDatabaseFile(pathPossibilityTwo))
			return null;
		List<PrivateMessage> messages = null;
		return messages;
	}
}
