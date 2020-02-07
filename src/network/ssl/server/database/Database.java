package network.ssl.server.database;

import java.io.File;
import java.io.IOException;

public abstract class Database<T extends DatabaseObject> extends StringDatabase {
	protected Class<T> databaseItemClass;
	
	public Database(Class<T> itemClass, File databaseFile) throws IOException {
		this(itemClass, databaseFile.getPath(), false);
	}
	
	public Database(Class<T> itemClass, String databaseFilePath) throws IOException {
		this(itemClass, databaseFilePath, false);
	}
	
	public Database(Class<T> itemClass, File databaseFile, boolean initFromFile) throws IOException {
		this(itemClass, databaseFile.getPath(), initFromFile);
	}
	
	public Database(Class<T> itemClass, String databaseFilePath, boolean initFromFile) throws IOException {
		super(databaseFilePath, initFromFile);
		databaseItemClass = itemClass;
	}
	
	public boolean addItem(T item) {
		return addItem(item.toDatabaseString());
	}
	
	public DatabaseObject getDatabaseItem(int index) {
		try {
			String stringItem = getItem(index);
			if(stringItem == null)
				return null;
			T databaseItem = databaseItemClass.getDeclaredConstructor().newInstance();
			databaseItem.initFromDatabaseString(stringItem);
			return databaseItem;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
