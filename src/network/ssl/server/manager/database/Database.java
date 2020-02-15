package network.ssl.server.manager.database;

import java.io.File;
import java.io.IOException;

import network.ssl.server.manager.stringDatabase.StringFileDatabase;

public abstract class Database<T extends StringDatabaseItem> extends StringFileDatabase {
	protected Class<T> databaseItemClass;
	
	public Database(Class<T> itemClass, File databaseFile) throws IOException {
		this(itemClass, databaseFile.getPath());
	}
	
	public Database(Class<T> itemClass, String databaseFilePath) throws IOException {
		super(databaseFilePath);
		databaseItemClass = itemClass;
	}
	
	public boolean addItem(T item) {
		return addItem(item.toDatabaseString());
	}
	
	public T getDatabaseItem(int index) {
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
