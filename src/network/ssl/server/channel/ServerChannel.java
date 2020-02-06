package network.ssl.server.channel;

import network.ssl.server.database.DatabaseObject;

public class ServerChannel implements DatabaseObject {
	private int id;
	private String name;
	

	@Override
	public String toDatabaseString() {
		return null;
	}

	@Override
	public int initFromDatabaseString(String databaseString) {
		return 0;
	}
	
}
