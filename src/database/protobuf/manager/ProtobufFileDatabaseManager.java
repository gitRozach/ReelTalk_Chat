package database.protobuf.manager;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;

public class ProtobufFileDatabaseManager implements Closeable {
	private List<ProtobufFileDatabase<?>> registeredDatabases;
	
	public ProtobufFileDatabaseManager() {
		registeredDatabases = new ArrayList<ProtobufFileDatabase<?>>();
	}
	
	public boolean registerDatabase(ProtobufFileDatabase<?> database) {
		if(database == null)
			return false;
		if(registeredDatabases.contains(database))
			return false;
		return registeredDatabases.add(database);
	}
	
	@Override
	public void close() throws IOException {
		registeredDatabases.forEach(a -> a.close());
	}
	
	public void loadItems() {
		registeredDatabases.forEach(a -> a.loadFileItems());
	}
	
	public boolean configureDatabasePath(String databaseName, String newPath) {
		if(databaseName == null || newPath == null)
			return false;
		for(ProtobufFileDatabase<?> currentDb : registeredDatabases) {
			if(currentDb.getName().equals(databaseName))
				return currentDb.setDatabaseFile(newPath);
		}
		return false;
	}
	
	public int removeRegisteredDatabase(String databaseName) {
		int index = indexOfRegisteredDatabase(databaseName);
		if(index != -1)
			removeRegisteredDatabase(index);
		return index;
	}
	
	public ProtobufFileDatabase<?> removeRegisteredDatabase(int index) {
		if(index < 0 || index >= registeredDatabases.size())
			return null;
		return registeredDatabases.remove(index);
	}
	
	public boolean hasRegisteredDatabase(String databaseName) {
		return indexOfRegisteredDatabase(databaseName) != -1;
	}
	
	public int indexOfRegisteredDatabase(String databaseName) {
		if(databaseName == null)
			return -1;
		for(int i = 0; i < registeredDatabases.size(); ++i)
			if(registeredDatabases.get(i).getName().equals(databaseName))
				return i;
		return -1;
	}
	
	public ProtobufFileDatabase<?> getRegisteredDatabase(String databaseName) {
		return getRegisteredDatabase(indexOfRegisteredDatabase(databaseName));
	}
	
	public ProtobufFileDatabase<?> getRegisteredDatabase(int index) {
		if(index < 0 || index >= registeredDatabases.size())
			return null;
		return registeredDatabases.get(index);
	}
	
	public List<ProtobufFileDatabase<?>> getRegisteredDatabases() {
		return registeredDatabases;
	}
}
