package database.protobuf.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;
import protobuf.ClientIdentities.ClientAccount;

public class ClientAccountDatabase extends ProtobufFileDatabase<ClientAccount> {
	private final Object idLock = new Object();
	
	public ClientAccountDatabase() throws IOException {
		this("", "");
	}
	
	public ClientAccountDatabase(String databaseName) throws IOException {
		this(databaseName, "");
	}
	
	public ClientAccountDatabase(String databaseName, String filePath) throws IOException {
		super(ClientAccount.class, databaseName, filePath);
	}
	
	@Override
	public void sort(List<ClientAccount> items) {
		items.sort(ClientAccountComparator);
	}
	
	public List<ClientAccount> getByBaseId(int id) {
		List<ClientAccount> resultList = new ArrayList<>();
		for(ClientAccount currentAccount : getLoadedItems()) {
			if(currentAccount.getProfile().getBase().getId() == id)
				resultList.add(currentAccount);
		}
		return resultList;
	}
	
	public List<ClientAccount> getByUsernameAndPassword(String username, String password) {
		List<ClientAccount> resultList = new ArrayList<>();
		for(ClientAccount currentAccount : getLoadedItems()) {
			if(currentAccount.getProfile().getBase().getUsername().equals(username) && currentAccount.getPassword().equals(password))
				resultList.add(currentAccount);
		}
		return resultList;
	}
	
	public int generateUniqueBaseId() {
		return getLoadedItems().isEmpty() ? 1 : generateUniqueBaseId(getItem(getLoadedItems().size() - 1).getProfile().getBase().getId() + 1); 
	}
	
	public int generateUniqueBaseId(int minId) {
		return generateUniqueBaseId(minId, Integer.MAX_VALUE);
	}
	
	public int generateUniqueBaseId(int minId, int maxId) {
		synchronized (idLock) {
			if(minId > maxId)
				return -1;
			if(getLoadedItems().isEmpty())
				return minId;
			boolean idAlreadyExists = false;
			for(int currentId = minId; currentId <= maxId; ++currentId) {
				idAlreadyExists = false;
				for(ClientAccount currentAccount : getLoadedItems()) {
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
	
	public static Comparator<ClientAccount> ClientAccountComparator = new Comparator<ClientAccount>() {
		@Override
		public int compare(ClientAccount o1, ClientAccount o2) {
			return o1.getProfile().getBase().getId() - o2.getProfile().getBase().getId();
		}
	};
}
