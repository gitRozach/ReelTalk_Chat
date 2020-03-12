package network.peer.server.database.protobuf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import protobuf.ClientIdentities.ClientAccount;

public class ClientAccountDatabase extends ProtobufFileDatabase<ClientAccount> {
	private final Object idLock = new Object();
	
	public ClientAccountDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ClientAccountDatabase(File databaseFile) throws IOException {
		super(ClientAccount.class, databaseFile);
		initialize();
	}	
	
	public List<ClientAccount> getByBaseId(int id) {
		List<ClientAccount> resultList = new ArrayList<>();
		for(ClientAccount currentAccount : getItems()) {
			if(currentAccount.getProfile().getBase().getId() == id)
				resultList.add(currentAccount);
		}
		return resultList;
	}
	
	public List<ClientAccount> getByUsernameAndPassword(String username, String password) {
		List<ClientAccount> resultList = new ArrayList<>();
		for(ClientAccount currentAccount : getItems()) {
			if(currentAccount.getProfile().getBase().getUsername().equals(username) && currentAccount.getPassword().equals(password))
				resultList.add(currentAccount);
		}
		return resultList;
	}
	
	public int generateUniqueBaseId() {
		return getItems().isEmpty() ? 1 : generateUniqueBaseId(getItem(getItems().size() - 1).getProfile().getBase().getId() + 1); 
	}
	
	public int generateUniqueBaseId(int minId) {
		return generateUniqueBaseId(minId, Integer.MAX_VALUE);
	}
	
	public int generateUniqueBaseId(int minId, int maxId) {
		synchronized (idLock) {
			if(minId > maxId)
				return -1;
			if(getItems().isEmpty())
				return minId;
			boolean idAlreadyExists = false;
			for(int currentId = minId; currentId <= maxId; ++currentId) {
				idAlreadyExists = false;
				for(ClientAccount currentAccount : getItems()) {
					if(currentAccount.getProfile().getBase().getId() == currentId) {
						idAlreadyExists = true;
						break;
					}
				}
				if(!idAlreadyExists)
					return currentId;
			}
			return -1;
		}
	}
}
