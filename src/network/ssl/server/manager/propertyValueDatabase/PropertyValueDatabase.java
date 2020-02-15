package network.ssl.server.manager.propertyValueDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import network.ssl.server.manager.database.Database;

public class PropertyValueDatabase<T extends PropertyValueDatabaseObject> extends Database<T> {
	
	public PropertyValueDatabase(Class<T> itemClass, String databaseFilePath) throws IOException {
		this(itemClass, new File(databaseFilePath));
	}
	
	public PropertyValueDatabase(Class<T> itemClass, File databaseFile) throws IOException {
		super(itemClass, databaseFile);
	}

	public static String getPropertyStart() {
		return PropertyValueDatabaseObject.PROPERTY_START;
	}
	
	public static String getPropertyEnd() {
		return PropertyValueDatabaseObject.PROPERTY_END;
	}
	
	public String[] getByProperty(String propertyName, String propertyValue) {
		try {
			ArrayList<String> resultList = new ArrayList<>();
			for(String databaseString : items) {
				String[] namesAndValues = databaseString.split(getPropertyEnd());
				
				for(String current : namesAndValues) {
					String currentSplit[] = current.split(getPropertyStart());
					String propName = currentSplit[0];
					String propValue = currentSplit[1];
					if(propName.equals(propertyName) && propValue.equals(propertyValue))
						resultList.add(databaseString);
				}
			}
			return resultList.toArray(new String[resultList.size()]);
		}
		catch(Exception e) {
			return null;
		}
	}
}
