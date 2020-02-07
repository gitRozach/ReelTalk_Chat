package network.ssl.client.id;

import java.io.Serializable;

import network.ssl.server.database.DatabaseObject;

public class ClientData implements Serializable, DatabaseObject {
	private static final long serialVersionUID = -1649645536049458115L;
	public static final String PROPERTY_START = "=";
	public static final String PROPERTY_END = "#";
	
	public static final String ID_PROPERTY_NAME = "id";
	public static final int DEFAULT_ID = -1;

	private int id;

	public ClientData() {
		this(DEFAULT_ID);
	}
	
	public ClientData(String databaseString) {
		this();
		initFromDatabaseString(databaseString);
	}

	public ClientData(int id) {
		this.id = id;
	}

	public boolean isDefaultInstance() {
		return id == DEFAULT_ID;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ClientData) {
			try {
				ClientData test = (ClientData) obj;
				return test.getId() == getId();
			} 
			catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Id: " + this.getId() + " ]";
	}

	@Override
	public String toDatabaseString() {
		return (ID_PROPERTY_NAME + PROPERTY_START + getId() + PROPERTY_END).trim();
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		String[] namesAndValues = databaseString.split(PROPERTY_END);
		int propSetCounter = 0;
		
		for(String current : namesAndValues) {
			try {
				String currentSplit[] = current.split(PROPERTY_START);
				String propName = currentSplit[0];
				String propValue = currentSplit[1];
				
				if(propName.equals(ID_PROPERTY_NAME)) {
					setId(Integer.parseInt(propValue));
					++propSetCounter;
				}
			}
			catch(Exception e) {
				continue;
			}
		}
		return propSetCounter;
	}

	public int getId() {
		return id;
	}

	public void setId(int value) {
		id = value;
	}
}