package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;

import protobuf.ClientIdentities.ClientAccount;

public class ClientAccountManager extends ProtobufFileDatabase<ClientAccount> {
	public ClientAccountManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ClientAccountManager(File databaseFile) throws IOException {
		super(ClientAccount.class, databaseFile);
	}	
	
	public ClientAccount getByUsernameAndPassword(String username, String password) {
		for(ClientAccount currentAccount : getItems()) {
			if(currentAccount.getProfile().getBase().getUsername().equals(username) && currentAccount.getPassword().equals(password))
				return currentAccount;
		}
		return null;
	}
	
	public int generateUniqueId() {
		return generateUniqueClientId(items.isEmpty() ? 1 : 0, Integer.MAX_VALUE);
	}
	
	public int generateUniqueId(int minId) {
		return generateUniqueClientId(minId, Integer.MAX_VALUE);
	}
	
	public synchronized int generateUniqueClientId(int minId, int maxId) {
		return 0;
	}
}
