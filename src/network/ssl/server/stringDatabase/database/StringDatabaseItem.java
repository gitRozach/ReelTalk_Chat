package network.ssl.server.stringDatabase.database;

public interface StringDatabaseItem {
	public String toDatabaseString();
	public int initFromDatabaseString(String databaseString);
}
