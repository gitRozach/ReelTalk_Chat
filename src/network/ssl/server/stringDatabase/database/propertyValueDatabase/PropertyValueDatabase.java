package network.ssl.server.stringDatabase.database.propertyValueDatabase;

import java.io.File;
import java.io.IOException;

import network.ssl.server.stringDatabase.database.Database;
import network.ssl.server.stringDatabase.database.StringDatabaseItem;

public class PropertyValueDatabase<T extends StringDatabaseItem> extends Database<T> {
	
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
}
