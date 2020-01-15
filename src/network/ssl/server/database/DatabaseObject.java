package network.ssl.server.database;

public interface DatabaseObject {
	public String toDatabaseString();
	public int initFromDatabaseString(String databaseString);
}
