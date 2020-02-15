package network.ssl.server.manager.clientDataManager.items;

import java.io.Serializable;
import java.util.Map;

import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabaseObject;

public abstract class ClientData extends PropertyValueDatabaseObject implements Serializable {
	private static final long serialVersionUID = -1649645536049458115L;
	public static final String ID_PROPERTY_NAME = "id";
	public static final int DEFAULT_ID = -1;

	protected int id;

	public ClientData() {
		this(DEFAULT_ID);
	}
	
	public ClientData(String databaseString) {
		initFromDatabaseString(databaseString);
	}

	public ClientData(int clientId) {
		id = clientId;
	}

	public boolean isDefaultInstance() {
		return id == DEFAULT_ID;
	}

	@Override
	public boolean equals(final Object obj) {
		return 	obj != null && obj instanceof ClientData && ((ClientData)obj).getId() == getId();
	}

	@Override
	public String toString() {
		return "[Id: " + getId() + " ]";
	}

	@Override
	public String toDatabaseString() {
		return ID_PROPERTY_NAME + PROPERTY_START + getId() + PROPERTY_END;
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(ID_PROPERTY_NAME)) {
			setId(Integer.parseInt(propertyValueMap.get(ID_PROPERTY_NAME)));
			++counter;
		}
		return counter;
	}

	public int getId() {
		return id;
	}

	public void setId(int value) {
		id = value;
	}
}