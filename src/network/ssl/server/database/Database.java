package network.ssl.server.database;

import java.io.File;
import java.io.IOException;

public class Database<T extends DatabaseObject> extends StringDatabase {
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
	
	public DatabaseObject getDatabaseItem(int index) throws IOException, InstantiationException, IllegalAccessException {
		return null;
	}
}
