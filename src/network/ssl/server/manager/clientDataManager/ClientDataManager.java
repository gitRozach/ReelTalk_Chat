package network.ssl.server.manager.clientDataManager;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.clientDataManager.items.BasicClientData;
import network.ssl.server.manager.clientDataManager.items.ClientData;
import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabase;

public class ClientDataManager<T extends ClientData> extends PropertyValueDatabase<T> {	
	public ClientDataManager(Class<T> itemClass, String databaseFilePath) throws IOException {
		this(itemClass, new File(databaseFilePath));
	}
	
	public ClientDataManager(Class<T> itemClass, File databaseFile) throws IOException {
		super(itemClass, databaseFile);
	}
	
	public int generateUniqueId() {
		return generateUniqueClientId(items.isEmpty() ? 1 : new BasicClientData(items.get(items.size() - 1)).getId() + 1, Integer.MAX_VALUE);
	}
	
	public int generateUniqueId(int minId) {
		return generateUniqueClientId(minId, Integer.MAX_VALUE);
	}
	
	public synchronized int generateUniqueClientId(int minId, int maxId) {
		if(minId > maxId)
			return -1;
		for(int a = minId; a <= maxId; ++a) {
			boolean found = false;
			for(int b = 0; b < items.size(); ++b) {
				if(new BasicClientData(items.get(b)).getId() == a) {
					found = true;
					break;
				}
			}
			if(!found)
				return a;
		}
		return -1;
	}
}
