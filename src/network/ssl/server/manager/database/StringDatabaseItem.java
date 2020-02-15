package network.ssl.server.manager.database;

import java.nio.charset.Charset;

public interface StringDatabaseItem {
	public Charset getEncoding();
	public String toDatabaseString();
	public int initFromDatabaseString(String databaseString);
}
