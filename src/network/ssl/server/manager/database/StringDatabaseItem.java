package network.ssl.server.manager.database;

public interface StringDatabaseItem {
	public String toDatabaseString();
	public int initFromDatabaseString(String databaseString);
}
