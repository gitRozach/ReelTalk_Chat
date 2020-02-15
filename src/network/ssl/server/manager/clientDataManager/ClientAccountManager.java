package network.ssl.server.manager.clientDataManager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.clientDataManager.items.ClientAccountData;

public class ClientAccountManager extends ClientDataManager<ClientAccountData> {
	public ClientAccountManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}
	
	public ClientAccountManager(File databaseFile) throws IOException {
		super(ClientAccountData.class, databaseFile);
	}
}
