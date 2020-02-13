package network.ssl.server.manager;

import java.io.File;
import java.io.IOException;

import network.ssl.client.id.ClientData;
import network.ssl.server.stringDatabase.database.propertyValueDatabase.PropertyValueDatabase;
import network.ssl.server.stringDatabase.database.propertyValueDatabase.PropertyValueDatabaseObject;

public class ClientDataManager<T extends ClientData> extends PropertyValueDatabase<T> {	
	public ClientDataManager(Class<T> itemClass, String databaseFilePath) throws IOException {
		this(itemClass, new File(databaseFilePath));
	}
	
	public ClientDataManager(Class<T> itemClass, File databaseFile) throws IOException {
		super(itemClass, databaseFile);
	}
	
	public String getByProperty(String propertyName, String propertyValue) {
		try {
			for(String databaseString : items) {
				String[] namesAndValues = databaseString.split(PropertyValueDatabaseObject.PROPERTY_END);
				
				for(String current : namesAndValues) {
					String currentSplit[] = current.split(PropertyValueDatabaseObject.PROPERTY_START);
					String propName = currentSplit[0];
					String propValue = currentSplit[1];
					if(propName.equals(propertyName) && propValue.equals(propertyValue))
						return databaseString;
				}
			}
			return null;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public int generateUniqueId() {
		return generateUniqueId(items.isEmpty() ? 1 : new ClientData(items.get(items.size() - 1)).getId() + 1, Integer.MAX_VALUE);
	}
	
	public int generateUniqueId(int minId) {
		return generateUniqueId(minId, Integer.MAX_VALUE);
	}
	
	public synchronized int generateUniqueId(int minId, int maxId) {
		if(minId > maxId)
			return -1;
		for(int a = minId; a <= maxId; ++a) {
			boolean found = false;
			for(int b = 0; b < items.size(); ++b) {
				if(new ClientData(items.get(b)).getId() == a) {
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
