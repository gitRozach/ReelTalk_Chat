package network.ssl.server.database;

import java.io.File;
import java.io.IOException;

import network.ssl.client.id.ClientData;

public class ClientDatabase<T extends ClientData> extends StringDatabase {
	public static final String ITEM_PROP_START = "=";
	public static final String ITEM_PROP_END = "#";
	
	public ClientDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath), true);
	}
	
	public ClientDatabase(String databaseFilePath, boolean initialize) throws IOException {
		this(new File(databaseFilePath), initialize);
	}
	
	public ClientDatabase(File databaseFile) throws IOException {
		this(databaseFile, false);
	}
	
	public ClientDatabase(File databaseFile, boolean initialize) throws IOException {
		super(databaseFile, initialize);
	}
	
	public boolean addItem(T data) throws IOException {
		return addItem(data.toDatabaseString());
	}
	
	public String getByProperty(String propertyName, String propertyValue) {
		try {
			for(String databaseString : items) {
				String[] namesAndValues = databaseString.split(ITEM_PROP_END);
				
				for(String current : namesAndValues) {
					String currentSplit[] = current.split(ITEM_PROP_START);
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
				ClientData currentData = new ClientData(items.get(b));
				if(currentData.getId() == a) {
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
